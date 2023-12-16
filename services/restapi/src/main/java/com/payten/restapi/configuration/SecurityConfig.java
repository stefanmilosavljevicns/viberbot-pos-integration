package com.payten.restapi.configuration;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.slf4j.LoggerFactory;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(request -> {
                            logger.info("Remote Address: " + request.getRemoteAddr());
                            logger.info("Server Name: " + request.getServerName());
                            return "10.0.1".contains(request.getRemoteAddr());
                        }).permitAll()                        .requestMatchers(request -> "10.0.1".contains(request.getServerName())).permitAll()
                        .requestMatchers(request -> "ws".contains(request.getScheme())).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

}