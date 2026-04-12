package com.clbooster.app.security;

import com.clbooster.app.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/signup", "/error", "/favicon.ico",
                "/manifest.webmanifest", "/sw.js", "/offline.html",
                "/images/**", "/icons/**", "/line-awesome/**",
                "/VAADIN/**", "/frontend/**", "/webjars/**")
            .permitAll());

        super.configure(http);
        setLoginView(http, LoginView.class);
    }
}
