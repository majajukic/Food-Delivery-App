package com.example.fooddeliveryapp.OrderService.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // authorization will be done per method
public class WebSecurityConfig {

	@Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequest ->
                authorizeRequest
                    .anyRequest().authenticated())  
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));  

        return http.build();
    }
}
