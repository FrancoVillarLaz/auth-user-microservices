package proyect1.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import proyect1.gateway.dto.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import org.springframework.util.AntPathMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static proyect1.gateway.utils.HeaderUtils.addHeadersAndContinue;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${auth.service.url:http://localhost:8081}")
    private String authServiceUrl;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth-service/**", // Excluye TODAS las rutas del auth-service
            // Swagger documentation paths for all services
            "/api/amenities-service/swagger-ui.html",
            "/api/amenities-service/swagger-ui/**",
            "/api/amenities-service/v3/api-docs/**",
            "/api/credits-service/swagger-ui.html",
            "/api/credits-service/swagger-ui/**",
            "/api/credits-service/v3/api-docs/**",
            "/api/encomienda-service/swagger-ui.html",
            "/api/encomienda-service/swagger-ui/**",
            "/api/encomienda-service/v3/api-docs/**",
            "/api/establecimiento-service/swagger-ui.html",
            "/api/establecimiento-service/swagger-ui/**",
            "/api/establecimiento-service/v3/api-docs/**",
            "/api/mercadopago-service/swagger-ui.html",
            "/api/mercadopago-service/swagger-ui/**",
            "/api/mercadopago-service/v3/api-docs/**",
            "/api/notifications-service/swagger-ui.html",
            "/api/notifications-service/swagger-ui/**",
            "/api/notifications-service/v3/api-docs/**",
            "/api/pdf-service/swagger-ui.html",
            "/api/pdf-service/swagger-ui/**",
            "/api/pdf-service/v3/api-docs/**",
            "/api/product-service/swagger-ui.html",
            "/api/product-service/swagger-ui/**",
            "/api/product-service/v3/api-docs/**",
            "/api/storage-service/swagger-ui.html",
            "/api/storage-service/swagger-ui/**",
            "/api/storage-service/v3/api-docs/**",
            "/api/trabajadores-service/swagger-ui.html",
            "/api/trabajadores-service/swagger-ui/**",
            "/api/trabajadores-service/v3/api-docs/**"
    );

        private final AntPathMatcher pathMatcher = new AntPathMatcher();
        private final WebClient authWebClient;

        public JwtAuthenticationFilter(@Value("${auth.service.url:http://auth-service:8080}") String authServiceUrl) {
            this.authServiceUrl = authServiceUrl;
            this.authWebClient = WebClient.builder()
                    .baseUrl(authServiceUrl)
                    .defaultHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            String path = exchange.getRequest().getPath().toString();

            if (EXCLUDED_PATHS.stream().anyMatch(excludedPath -> pathMatcher.match(excludedPath, path))) {
                return chain.filter(exchange);
            }

            String idSuscripcion = exchange.getRequest().getHeaders().getFirst("X-Subscription-Id");
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            log.info(authHeader);

            log.info("con substring: "+ authHeader.substring(7));

            //hay que mapear el json
            Map<String, String> body = Collections.singletonMap("Token", authHeader.substring(7));

            log.info(body.toString());
            return authWebClient
                    .post()
                    .uri("/api/auth-service/validate-gateway")
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        System.err.println("Error validating token: " + response.statusCode());
                        return Mono.error(new ResponseStatusException(response.statusCode(), "Token inválido"));
                    })
                    .bodyToMono(TokenResponse.class)
                    .doOnError(error -> {
                        System.err.println("Error connecting to auth service: " + error.getMessage());
                        error.printStackTrace();
                    })
                    .flatMap(tokenInfo -> {
                        if (!tokenInfo.active()) {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }

                        if (idSuscripcion != null && !idSuscripcion.isEmpty()) {
                            String userId = tokenInfo.userId();

                            WebClient userClient = WebClient.builder()
                                    .baseUrl("lb://USER-SERVICE")
                                    .build();

                            return userClient.get()
                                    .uri("/api/users/by-subscription/info?idSuscripcion="+ idSuscripcion)
                                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                                    .header("X-User-Id", userId)
                                    .retrieve()
                                    .onStatus(HttpStatusCode::isError, response ->
                                            Mono.error(new ResponseStatusException(
                                                    HttpStatus.FORBIDDEN,
                                                    "Acceso denegado a la suscripción"
                                            ))
                                    )
                                    .bodyToMono(JsonNode.class)
                                    .flatMap(userInfoNode ->
                                            addHeadersAndContinue(exchange, chain, tokenInfo, userInfoNode)
                                    );
                        } else {
                            return addHeadersAndContinue(exchange, chain, tokenInfo, null);
                        }
                    })
                    .onErrorResume(ResponseStatusException.class, ex -> {
                        exchange.getResponse().setStatusCode(ex.getStatusCode());
                        return exchange.getResponse().setComplete();
                    })
                    .onErrorResume(Exception.class, ex -> {
                        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                        return exchange.getResponse().setComplete();
                    });
        }


        @Override
        public int getOrder() {
            return -1;
        }
    }