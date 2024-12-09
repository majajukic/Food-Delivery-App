package com.fooddeliveryservice.cloudgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class CloudGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudGatewayApplication.class, args);
	}
	
	/**
	 * Configures a KeyResolver for rate limiting based on user identity.
	 *
	 * This method returns a KeyResolver that extracts a unique key for each user
	 * making a request. It leverages the ReactiveSecurityContextHolder to retrieve
	 * the security context of the current request. The unique key is extracted
	 * from the "sub" claim of the JWT token, which typically represents the user's
	 * ID (email in this case). If the request is unauthenticated, it defaults to returning "anonymous".
	 *
	 * The KeyResolver is used by the rate limiter to apply per-user rate limits (1 request per 1 second per user --> see application.yaml).
	 *
	 * @return A KeyResolver that resolves user keys for rate limiting.
	 */
	@Bean
	KeyResolver userKeySolver() {
	    return exchange -> ReactiveSecurityContextHolder.getContext()
	            .flatMap(securityContext -> {
	                var authentication = securityContext.getAuthentication();
	                if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
	                    Jwt jwt = (Jwt) authentication.getPrincipal();
	                    String userId = jwt.getClaimAsString("sub"); 
	                    return Mono.just(userId);
	                } else {
	                    return Mono.just("anonymous");
	                }
	            })
	            .switchIfEmpty(Mono.just("anonymous"));
	}

	/**
	 * Configures a default Customizer for the Resilience4J Circuit Breaker.
	 *
	 * This method sets up a custom configuration for the Resilience4J Circuit Breaker
	 * used by the application. It defines a default configuration applied to all
	 * circuit breakers created by the application. The configuration uses the
	 * default settings provided by Resilience4J.
	 *
	 * The Circuit Breaker protects downstream services by managing failures and
	 * providing fallback mechanisms when services are unavailable.
	 *
	 * @return A Customizer for the Resilience4JCircuitBreakerFactory.
	 */
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(
				id -> new Resilience4JConfigBuilder(id)
						.circuitBreakerConfig(
								CircuitBreakerConfig.ofDefaults()

						).build()
		);
	}
}
