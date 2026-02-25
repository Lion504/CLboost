package com.clbooster.app.views;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.*;
//in your root terminal, run:
// mvn "-Dtest=com.clbooster.app.views.CLgenerator_CLITest" test
class CLgenerator_CLITest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void testFullUserFlow_login_generate_logout_exit() throws Exception {

        // Load job posting text from uploaded file
        String jobPosting = Files.readString(
                Paths.get("D:\\uni\\CLBooster\\CLboost\\src\\test\\resources\\sample_job_posting.txt")
        );

        // Build simulated console input in exact order your CLI expects
        String simulatedInput = String.join("\n",
                "fake-api-key",           // API key prompt
                "1",                      // Main menu -> Login
                "hihi",                   // Username
                "HIhi12345!67",           // Password
                "2",                      // Logged-in menu -> Generate Cover Letter
                "C:/Users/kiava/OneDrive/Documents/Kiavash_Montazeri_CVJan2026.pdf",                      // Resume root address
                jobPosting,               // Job posting text
                "3",                      // Logout
                "3"                       // Exit program
        ) + "\n";

        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Run the program
        CLgenerator_CLI.main(new String[]{});

        String output = outputStream.toString();

        // Basic flow assertions
        assertTrue(output.contains("CL GENERATOR - COMMAND LINE"));
        assertTrue(output.contains("Login"));
        assertTrue(output.contains("Generate"));
        assertTrue(output.contains("Logout"));
        assertTrue(output.contains("Goodbye"));
    }
}