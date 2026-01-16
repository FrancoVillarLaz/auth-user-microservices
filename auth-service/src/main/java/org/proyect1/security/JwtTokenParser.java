package org.proyect1.security;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonNumber;

import java.time.Instant;

@ApplicationScoped
public class JwtTokenParser {

    @Inject
    JWTParser parser;

    public SecurityUser parseAndValidate(String token) {
        try {
            var jwt = parser.parse(token);

            String type = jwt.getClaim("type");
            if (!"access".equals(type)) {
                throw new SecurityException("Token inválido para este recurso");
            }

            Long exp = jwt.getExpirationTime();
            long now = Instant.now().getEpochSecond();

            if (exp == null || exp < now) {
                throw new SecurityException("Token expirado");
            }

            Object userIdClaim = jwt.getClaim("userId");
            Long userId = null;

            if (userIdClaim instanceof Number n) {
                userId = n.longValue();
            } else if (userIdClaim instanceof JsonNumber jsonNum) {
                userId = jsonNum.longValue();
            } else if (userIdClaim != null) {
                userId = Long.parseLong(userIdClaim.toString());
            }

            if (userId == null) {
                throw new SecurityException("Token inválido: userId ausente o inválido");
            }

            String username = jwt.getClaim("username");
            if (username == null) {
                username = String.valueOf(userId);
            }

            String role = jwt.getGroups().stream().findFirst().orElse("USER");

            return new SecurityUser(
                    userId,
                    username,
                    role
            );

        } catch (ParseException e) {
            throw new SecurityException("Token inválido: no se pudo parsear", e);
        }
    }
}