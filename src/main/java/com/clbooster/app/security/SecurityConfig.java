package com.clbooster.app.security;

import com.clbooster.app.views.LoginView;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/signup", "/error", "/favicon.ico",
                "/manifest.webmanifest", "/sw.js", "/offline.html",
                "/images/**", "/icons/**", "/line-awesome/**",
                "/VAADIN/**", "/frontend/**", "/webjars/**")
            .permitAll());

        http.with(VaadinSecurityConfigurer.vaadin(), configurer ->
            configurer.loginView(LoginView.class));

        return http.build();
    }
}
