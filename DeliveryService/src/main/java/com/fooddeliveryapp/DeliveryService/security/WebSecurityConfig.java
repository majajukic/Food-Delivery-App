package com.fooddeliveryapp.DeliveryService.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // Marks this class as a configuration class to define security-related beans
@EnableWebSecurity  // Enables Spring Security for the application
@EnableMethodSecurity(prePostEnabled = true)  // Enables method-level security (e.g., @PreAuthorize annotations)
public class WebSecurityConfig {

	/**
     * Configures HTTP security for the application, including authentication and authorization.
     *
     * @param http The HttpSecurity object that is used to configure security settings.
     * @return A SecurityFilterChain that applies the security configuration.
     * @throws Exception If any error occurs during security configuration.
     */
	@Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequest ->
                authorizeRequest.requestMatchers("/deliveries/**")
                				.hasAuthority("SCOPE_internal")
                				.anyRequest()
                				.authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));  

        return http.build();
    }
}