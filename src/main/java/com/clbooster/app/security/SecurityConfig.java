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
        // Allow public access to static assets and landing page
        http.authorizeHttpRequests(auth ->
            auth.requestMatchers("/", "/signup", "/images/**").permitAll()
        );
        super.configure(http);
        // Redirect to LoginView for protected routes
        setLoginView(http, LoginView.class);
    }
}
