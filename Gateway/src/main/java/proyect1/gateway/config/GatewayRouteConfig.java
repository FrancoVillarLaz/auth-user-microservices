package proyect1.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// aca podemos redefinir rutas, por ahora default
@Configuration
public class GatewayRouteConfig {

    @Value("${auth.service.url:http://auth-service:8080}")
    private String authServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ===============================================
                // Auth Service - Rutas PÚBLICAS
                // IMPORTANTE: Estas rutas NO deben pasar por el JwtAuthenticationFilter
                // ===============================================
                .route("auth-service", r -> r
                        .path(
                                "/api/auth-service/register",
                                "/api/auth-service/login",
                                "/api/auth-service/validate",
                                "/api/auth-service/health"
                        )
                        .filters(f -> f.rewritePath("/api/auth-service/(?<segment>.*)", "/api/auth-service/${segment}"))
                        .uri(authServiceUrl))

                // ===============================================
                // Auth Service - Rutas AUTENTICADAS
                // Estas SÍ pasan por el JwtAuthenticationFilter
                // ===============================================
                .route("auth-service", r -> r
                        .path(
                                "/api/auth-service/refresh",
                                "/api/auth-service/logout",
                                "/api/auth-service/me",
                                "/api/auth-service/introspect",
                                "/api/auth-service/validate-gateway",
                                "/api/auth-service/subscriptions/**"
                        )
                        .filters(f -> f.rewritePath("/api/auth-service/(?<segment>.*)", "/api/auth-service/${segment}"))
                        .uri(authServiceUrl))
                .build();
    }
}
