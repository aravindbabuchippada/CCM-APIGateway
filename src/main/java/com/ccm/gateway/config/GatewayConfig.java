package com.ccm.gateway.config;

import com.ccm.gateway.exception.RouteConfigurationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import static com.ccm.gateway.constants.GatewayConstants.*;

@Configuration
@EnableWebFluxSecurity
public class GatewayConfig {

    private final ServerConfig serverConfig;

    public GatewayConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        try {
            return builder.routes()
                    // Security service routes
                    .route("security-service", r -> r
                            .path(BASE_PATH_SECURITY)
                            .filters(f -> f
                                    .rewritePath("/ccm/security/(?<remaining>.*)", REWRITE_SECURITY)
                                    .addRequestHeader(X_GATEWAY_HEADER, GATEWAY_NAME)
                                    .circuitBreaker(c -> c
                                            .setName("securityCB")
                                            .setFallbackUri("forward:/fallback/security"))
                                    .retry(c -> c
                                            .setRetries(2)
                                            .setMethods(HttpMethod.GET)
                                            .setExceptions(IOException.class, TimeoutException.class)))
                            .uri(serverConfig.getSecurityUri()))

                    // Client service routes
                    .route("client-service", r -> r
                            .path(BASE_PATH_CLIENT)
                            .filters(f -> f
                                    .rewritePath("/ccm/client/(?<remaining>.*)", REWRITE_CLIENT)
                                    .addRequestHeader(X_GATEWAY_HEADER, GATEWAY_NAME)
                                    .circuitBreaker(c -> c
                                            .setName("clientCB")
                                            .setFallbackUri("forward:/fallback/client"))
                                    .retry(c -> c
                                            .setRetries(2)
                                            .setMethods(HttpMethod.GET)
                                            .setExceptions(IOException.class, TimeoutException.class)))
                            .uri(serverConfig.getClientUri()))

                    // Customer service routes
                    .route("customer-service", r -> r
                            .path(BASE_PATH_CUSTOMER)
                            .filters(f -> f
                                    .rewritePath("/ccm/customer/(?<remaining>.*)", REWRITE_CUSTOMER)
                                    .addRequestHeader(X_GATEWAY_HEADER, GATEWAY_NAME)
                                    .circuitBreaker(c -> c
                                            .setName("customerCB")
                                            .setFallbackUri("forward:/fallback/customer"))
                                    .retry(c -> c
                                            .setRetries(2)
                                            .setMethods(HttpMethod.GET)
                                            .setExceptions(IOException.class, TimeoutException.class)))
                            .uri(serverConfig.getCustomerUri()))
                    .build();
        } catch (Exception e) {
            throw new RouteConfigurationException("Failed to configure route locator", e);
        }
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults()) // will pick up the CorsConfigurationSource bean
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/ccm/**","/actuator/**").permitAll() // Allow all microservices, actuator
                        .anyExchange().authenticated()
                ).build();
    }

    // Global CORS config
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Arrays.asList("*")); // Allow all origins
        corsConfig.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
