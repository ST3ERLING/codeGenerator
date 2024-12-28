package emsi.gateway.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.Collections;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("code_generator_route", r -> r.path("/code-generator/**")
						.filters(f -> f.stripPrefix(1)) // Removes "/code-generator" from the path
						.uri("lb://CODE-GENERATOR-SERVICE")) // Load-balances to the service
				.route("prompt_service_route", r -> r.path("/prompt/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://PROMPT-SERVICE")) // Load-balances to the service
				.route("user_service_route", r -> r.path("/users/**")
						.uri("lb://USER-SERVICE")) // Load-balances to the service
				.build();
	}
}
