package com.payten.restapi.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(request -> {
                            logger.info("SEMA"+request.getScheme());
                            logger.info("PROTOKOL"+request.getProtocol());
                            return request.getRemoteHost().contains("10.0.1");
                        }).permitAll()
                        // Allow all requests from localhost (both HTTP and HTTPS)
                        .requestMatchers("http://localhost/**", "https://localhost/**").permitAll()
                        // Allow WebSockets (if needed)
                        .requestMatchers(request -> "ws".equalsIgnoreCase(request.getScheme()) || "wss".equalsIgnoreCase(request.getScheme())).permitAll()
                        // Require authentication for all other requests
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

}