package com.payten.restapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(request -> 9943==request.getServerPort()).permitAll()
                        .requestMatchers(request -> "localhost".equals(request.getServerName())).permitAll()
                        .requestMatchers(request -> "ws".contains(request.getScheme())).permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

}