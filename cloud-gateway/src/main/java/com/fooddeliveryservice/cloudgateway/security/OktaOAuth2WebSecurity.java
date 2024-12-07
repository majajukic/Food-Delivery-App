package com.fooddeliveryservice.cloudgateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration  // Marks this class as a configuration class that can define beans
@EnableWebFluxSecurity  // Enables WebFlux Security, which is required for reactive applications
public class OktaOAuth2WebSecurity {

    /**
     * Configures the security filter chain for WebFlux.
     *
     * @param http The ServerHttpSecurity object that is used to configure security settings.
     * @return A SecurityWebFilterChain that applies the security configuration.
     */
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        http
            // disabling CSRF protection, not needed for jwt auth
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                .anyExchange().authenticated()
            )
            .oauth2Login(Customizer.withDefaults())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()) 
            );

        return http.build();
    }
}
