package com.clbooster.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/signup", "/error", "/favicon.ico",
                "/manifest.webmanifest", "/sw.js", "/offline.html",
                "/images/**", "/icons/**", "/line-awesome/**",
                "/VAADIN/**", "/frontend/**", "/webjars/**")
            .permitAll()
            .anyRequest()
            .authenticated())
            .formLogin(Customizer.withDefaults())
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
