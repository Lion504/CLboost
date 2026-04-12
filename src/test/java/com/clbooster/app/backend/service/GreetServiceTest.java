package com.clbooster.app.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreetServiceTest {

    private final GreetService service = new GreetService();

    @Test
    void greet_returnsAnonymousForNullName() {
        assertEquals("Hello anonymous user", service.greet(null));
    }

    @Test
    void greet_returnsAnonymousForEmptyName() {
        assertEquals("Hello anonymous user", service.greet(""));
    }

    @Test
    void greet_returnsPersonalizedGreetingForNonEmptyName() {
        assertEquals("Hello Timo", service.greet("Timo"));
    }
}
