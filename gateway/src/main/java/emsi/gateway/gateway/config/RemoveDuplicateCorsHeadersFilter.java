package emsi.gateway.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RemoveDuplicateCorsHeadersFilter {

    @Bean
    public GlobalFilter filter() {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();

            // Remove duplicate Access-Control-Allow-Origin headers
            response.getHeaders().remove("Access-Control-Allow-Origin");

            // Add only a single allowed origin
            response.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000");
        }));
    }
}

