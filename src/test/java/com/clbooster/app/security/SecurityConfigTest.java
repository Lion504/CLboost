package com.clbooster.app.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityConfigTest {

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();

    @Test
    void publicLoginRoute_isAccessibleWithoutAuth() throws IOException, InterruptedException {
        HttpResponse<Void> response = get("/login");
        assertEquals(200, response.statusCode());
    }

    @Test
    void protectedRoute_redirectsToLoginWithoutAuth() throws IOException, InterruptedException {
        HttpResponse<Void> response = get("/dashboard");

        int status = response.statusCode();
        assertTrue(status >= 300 && status < 400);

        String location = response.headers().firstValue("Location").orElse("");
        assertTrue(location.contains("/login"));
    }

    private HttpResponse<Void> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + path)).GET().build();

        return client.send(request, HttpResponse.BodyHandlers.discarding());
    }
}
