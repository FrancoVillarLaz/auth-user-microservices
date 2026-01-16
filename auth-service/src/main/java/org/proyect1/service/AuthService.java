package org.proyect1.service;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.proyect1.dto.auth.ParsedToken;
import org.proyect1.dto.auth.request.LoginRequest;
import org.proyect1.dto.auth.request.RegisterRequest;
import org.proyect1.dto.auth.response.TokenResponse;
import org.proyect1.dto.event.AuthEvent;
import org.proyect1.entity.User;
import org.proyect1.entity.UserLastModification;
import org.proyect1.entity.UserSubscription;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@ApplicationScoped
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Inject JwtService jwtService;
    @Inject RedisService redisService;
    @Inject JWTParser jwtParser;
    @Inject KafkaEventProducer kafkaEventProducer;
    @Inject KafkaEventAwaiter kafkaEventAwaiter;
    @Inject SubscriptionService subscriptionService;

    @Transactional
    public User persistUser(RegisterRequest request) {

        Optional<User> existingUser = User.findByUsername(request.username)
                .or(() -> User.findByEmail(request.email));

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (user.lastModification == UserLastModification.ROLLBACK_USER_CREATION) {
                log.info("Reactivando usuario tras rollback de saga: {}", user.id);

                user.isActive = true;
                user.lastModification = UserLastModification.REACTIVATION;
                user.persist();

                return user;
            }

            if (request.suscriptionId != null) {
                validateUserSubscription(user.id, request.suscriptionId);
                subscriptionService.createUserSubscription(user.id, request.suscriptionId);
                return user;
            }


            throw new BadRequestException("El usuario ya existe");
        }

        User newUser = new User();
        newUser.username = request.username;
        newUser.email = request.email;
        newUser.password = BCrypt.hashpw(request.password, BCrypt.gensalt());
        newUser.role = "USER";
        newUser.isActive = true;
        newUser.lastModification = UserLastModification.USER_CREATION;

        try {
            newUser.persist();
        } catch (PersistenceException e) {
            throw new BadRequestException("El usuario ya existe");
        }

        return newUser;
    }


    private void validateUserSubscription(Long userId, String subscriptionId) {

        Optional<UserSubscription> subscription =
                UserSubscription.findByUserAndSub(userId, subscriptionId);

        if (subscription.isEmpty()) {
            return;
        }

        UserSubscription sub = subscription.get();

        if (!sub.idSuscripcion.equals(subscriptionId)){
            return;
        }
        if (sub.isActive) {
            log.info("Usuario {} ya tiene suscripción activa {}", userId, subscriptionId);
            throw new BadRequestException("El usuario ya tiene esta suscripción activa");
        }

        log.info("Usuario {} tiene suscripción inactiva {}", userId, subscriptionId);
        throw new BadRequestException("El usuario tiene la suscripción inactiva");
    }


    public TokenResponse register(RegisterRequest request) {

        User user = persistUser(request);

        String eventId = UUID.randomUUID().toString();

        CompletableFuture<AuthEvent> future =
                kafkaEventAwaiter.await(eventId);

        kafkaEventProducer.publishUserRegistered(
                eventId,
                user.id,
                user.username,
                user.email,
                request.suscriptionId,
                null, // IP vendrá del gateway
                request.userMetaData
        );

        try {
            AuthEvent response = future.get(10, TimeUnit.SECONDS);

            switch (response.getEventType()) {
                case USER_CREATION_SUCCESS:
                    return generateTokenResponse(user);

                case USER_CREATION_FAILED:
                    throw new RuntimeException(
                            "Creación fallida en downstream: " + response.getMetadata()
                    );

                default:
                    throw new RuntimeException(
                            "Evento inesperado: " + response.getEventType()
                    );
            }

        } catch (TimeoutException e) {
            future.cancel(true);
            kafkaEventAwaiter.fail(eventId, e);
            throw new RuntimeException("Timeout esperando respuesta USER_CREATION", e);

        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            future.cancel(true);
            kafkaEventAwaiter.fail(eventId, cause);
            throw new RuntimeException("Error ejecutando respuesta USER_CREATION", cause);

        } catch (InterruptedException e) {
            future.cancel(true);
            kafkaEventAwaiter.fail(eventId, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrumpido esperando respuesta USER_CREATION", e);

        } catch (RuntimeException e) {
            future.cancel(true);
            kafkaEventAwaiter.fail(eventId, e);
            throw e;

        } finally {
            if (!future.isDone()) {
                future.cancel(true);
                kafkaEventAwaiter.fail(eventId,
                        new RuntimeException("Awaiter cleanup: cancelado"));
            }
        }
    }


    public TokenResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = User.findByUsernameOrEmail(request.identifier)
                .orElseThrow(() -> {
                    kafkaEventProducer.publishLoginFailed(request.identifier, ipAddress, "Usuario no encontrado");
                    return new NotAuthorizedException("Credenciales inválidas");
                });

        if (!user.isActive) {
            kafkaEventProducer.publishLoginFailed(request.identifier, ipAddress, "Usuario inactivo");
            throw new NotAuthorizedException("Usuario inactivo");
        }

        if (!BCrypt.checkpw(request.password, user.password)) {
            kafkaEventProducer.publishLoginFailed(request.identifier, ipAddress, "Password incorrecto");
            throw new NotAuthorizedException("Credenciales inválidas");
        }

        kafkaEventProducer.publishUserLoggedIn(user.id, user.username, user.email, ipAddress, userAgent);

        return generateTokenResponse(user);
    }

    public TokenResponse refreshToken(String refreshToken) {
        try {
            JsonWebToken jwt = jwtParser.parse(refreshToken);
            String tokenType = jwt.getClaim("type");

            if (!"refresh".equals(tokenType)) {
                throw new NotAuthorizedException("Token inválido: no es refresh");
            }

            Object userIdClaim = jwt.getClaim("userId");
            Long userId;

            if (userIdClaim instanceof Number n) {
                userId = n.longValue();
            } else if (userIdClaim instanceof jakarta.json.JsonNumber jsonNumber) {
                userId = jsonNumber.longValue();
            } else {
                throw new IllegalStateException("userId claim con tipo inesperado: " +
                        (userIdClaim != null ? userIdClaim.getClass() : "null"));
            }

            String storedToken = redisService.getRefreshToken(userId.toString());
            if (storedToken == null || !storedToken.equals(refreshToken)) {
                throw new NotAuthorizedException("Refresh token inválido o expirado");
            }

            if (redisService.isTokenBlacklisted(refreshToken)) {
                throw new NotAuthorizedException("Token revocado");
            }

            User user = User.findById(userId);
            if (user == null || !user.isActive) {
                throw new NotAuthorizedException("Usuario no encontrado o inactivo");
            }

            kafkaEventProducer.publishTokenRefreshed(user.id, user.username);
            return generateTokenResponse(user);

        } catch (ParseException e) {
            throw new NotAuthorizedException("Token inválido");
        }
    }

    public void logout(String userId, String username, String accessToken, String refreshToken, String ipAddress) {
        redisService.deleteRefreshToken(userId);

        if (accessToken != null) {
            redisService.blacklistToken(accessToken, jwtService.getAccessTokenExpiration());
        }

        if (refreshToken != null) {
            redisService.blacklistToken(refreshToken, jwtService.getRefreshTokenExpiration());
        }

        kafkaEventProducer.publishUserLoggedOut(Long.parseLong(userId), username, ipAddress);
    }

    private TokenResponse generateTokenResponse(User user) {
        Set<String> activeSubscriptions = subscriptionService.getUserSubscriptions(user.id);
        String accessToken = jwtService.generateAccessToken(user, activeSubscriptions);

        String refreshToken = jwtService.generateRefreshToken(user);
        redisService.saveRefreshToken(user.id, refreshToken);

        return new TokenResponse(accessToken, refreshToken, jwtService.getAccessTokenExpiration());
    }
    /** ✅ Corregido: ahora retorna ParsedToken */
    public ParsedToken validarAndParser(String authHeaderOrToken) throws ParseException {
        String token = authHeaderOrToken;

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isBlank()) {
            throw new NotAuthorizedException("Token ausente");
        }

        return jwtService.parseAndValidateToken(token);
    }

    public boolean validar(String authHeaderOrToken) {
        String token = authHeaderOrToken;

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isBlank()) {
            return false;
        }

        return jwtService.ValidateToken(token);
    }
}
