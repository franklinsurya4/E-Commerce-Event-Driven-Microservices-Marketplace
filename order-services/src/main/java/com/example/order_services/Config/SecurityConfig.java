package com.example.order_services.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // THIS is what stops 401
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/order/**",
                                "/actuator/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )

                //  IMPORTANT: completely disable oauth2
                .oauth2ResourceServer(oauth2 -> oauth2.disable());

        return http.build();
    }
}
