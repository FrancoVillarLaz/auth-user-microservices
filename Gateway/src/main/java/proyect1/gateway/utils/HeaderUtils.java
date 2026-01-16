package proyect1.gateway.utils;

import com.fasterxml.jackson.databind.JsonNode;
import proyect1.gateway.dto.TokenResponse;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.Map;

public class HeaderUtils {

    /**
     * Convierte una cadena de Camel Case a Snake Case,
     * útil para nombres de headers.
     * Ejemplo: "userId" -> "user-id"
     */
    public static String camelToSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return "";
        }
        String snakeCase = camelCase.replaceAll("([A-Z])", "-$1").toLowerCase();

        if (snakeCase.startsWith("-")) {
            return snakeCase.substring(1);
        }
        return snakeCase;
    }

    public static Mono<Void> addHeadersAndContinue(
            ServerWebExchange exchange,
            GatewayFilterChain chain,
            TokenResponse tokenInfo,
            JsonNode userInfoNode) {

        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();

        requestBuilder.header("X-User-Id", String.valueOf(tokenInfo.userId()));
        requestBuilder.header("X-Rol", String.join(",", tokenInfo.role()));

        if (userInfoNode != null && userInfoNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = userInfoNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String headerName = "X-" + HeaderUtils.camelToSnakeCase(field.getKey());
                requestBuilder.header(headerName, field.getValue().asText(""));
            }
        }

        ServerHttpRequest modifiedRequest = requestBuilder.build();

        return chain.filter(
                exchange.mutate()
                        .request(modifiedRequest)
                        .build()
        );
    }

}
