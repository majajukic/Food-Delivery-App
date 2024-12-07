package com.fooddeliveryapp.RestaurantService.security;

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
     * Defines the security filter chain for HTTP requests.
     * Configures authorization rules and OAuth2 resource server integration for JWT authentication.
     * 
     * @param http HttpSecurity object to configure security for HTTP requests.
     * @return SecurityFilterChain object representing the configured security rules.
     * @throws Exception in case of any errors during configuration.
     */
	  @Bean
	    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
	        http
	            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) 
	            
	            .authorizeHttpRequests(authorizeRequests -> 
	                authorizeRequests.anyRequest().authenticated() 
	            );

	        return http.build();
	    }
}
