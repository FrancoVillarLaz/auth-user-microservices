package org.proyect1.service;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.proyect1.dto.auth.ParsedToken;
import org.proyect1.entity.User;

import java.time.Instant;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "smallrye.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "jwt.access.token.expiration")
    Long accessTokenExpiration;

    @ConfigProperty(name = "jwt.refresh.token.expiration")
    Long refreshTokenExpiration;

    @Inject
    JWTParser jwtParser;

    @Inject
    JsonWebToken jsonWebToken;

    public String generateAccessToken(User user, Set<String> subscriptions) {
        return Jwt.issuer(issuer)
                .claim("userId", user.id.toString())
                .claim("subscriptions", subscriptions)
                .claim("role", user.role)
                .claim("type", "access")
                .expiresAt(Instant.now().plusSeconds(accessTokenExpiration))
                .sign();
    }


    public String generateRefreshToken(User user) {
        return Jwt.issuer(issuer)
                .upn(user.username)
                .claim("userId", user.id)
                .claim("type", "refresh")
                .expiresAt(Instant.now().plusSeconds(refreshTokenExpiration))
                .sign();
    }

    public Long getAccessTokenExpiration() { return accessTokenExpiration; }
    public Long getRefreshTokenExpiration() { return refreshTokenExpiration; }

    public ParsedToken parseAndValidateToken(String token) throws ParseException, SecurityException {
        JsonWebToken jwt = (JsonWebToken) jwtParser.parse(token);

        // Validar tipo de token
        String type = jwt.getClaim("type");
        if (!"access".equals(type)) {
            throw new SecurityException("Token inválido para este recurso");
        }

        Long exp = jwt.getExpirationTime();
        if (exp == null || exp < Instant.now().getEpochSecond()) {
            throw new SecurityException("Token expirado");
        }

        Object userId = jwt.getClaim("userId");
        if (userId == null) {
            throw new SecurityException("Token inválido: userId ausente");
        }

        ParsedToken parsed = new ParsedToken();
        parsed.userId = Long.valueOf(userId.toString());
        parsed.role = jwt.getClaim("role");
        parsed.issuer = jwt.getIssuer();
        parsed.expiresAt = Instant.ofEpochSecond(exp);

        return parsed;
    }

    public boolean ValidateToken(String token) {
        try {
            JsonWebToken jwt = (JsonWebToken) jwtParser.parse(token);

            // Validar tipo de token
            String type = jwt.getClaim("type");
            if (!"access".equals(type)) {
                return false;
            }

            Long exp = jwt.getExpirationTime();
            if (exp == null || exp < Instant.now().getEpochSecond()) {
                return false;
            }

            Object userId = jwt.getClaim("userId");
            if (userId == null) {
                return false;
            }

            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    public ParsedToken parseFromContext() {
        if (jsonWebToken == null) {
            throw new NotAuthorizedException("No hay token JWT en el contexto actual");
        }

        ParsedToken parsed = new ParsedToken();
        parsed.userId = jsonWebToken.getClaim("userId");
        parsed.role = jsonWebToken.getClaim("role");
        parsed.issuer = jsonWebToken.getIssuer();
        parsed.expiresAt = Instant.ofEpochSecond(jsonWebToken.getExpirationTime());

        return parsed;
    }
}
