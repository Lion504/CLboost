package com.clbooster.app;

import com.vaadin.flow.component.html.testbench.H1Element;
import com.vaadin.flow.component.html.testbench.ParagraphElement;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.BrowserTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class DashboardViewIT extends BrowserTestBase {

    /**
     * If running on CI, get the host name from environment variable HOSTNAME
     *
     * @return the host name
     */
    private static String getDeploymentHostname() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname != null && !hostname.isEmpty()) {
            return hostname;
        }
        return "localhost";
    }

    @BeforeEach
    public void open() {
        getDriver().get("http://" + getDeploymentHostname() + ":8080/");
    }

    @BrowserTest
    public void dashboardLoadsAndShowsWelcomeContent() {
        // Check that the main heading is present
        H1Element heading = $(H1Element.class).waitForFirst();
        Assertions.assertEquals("Welcome to CL Booster", heading.getText());

        // Check that welcome paragraphs are present
        Assertions.assertTrue($(ParagraphElement.class).exists());
        Assertions.assertTrue($(ParagraphElement.class).all().size() >= 2);

        // Check that at least one paragraph contains expected content
        boolean foundWelcomeText = $(ParagraphElement.class).all().stream()
                .anyMatch(p -> p.getText().contains("AI-Powered Cover Letter Generator"));
        Assertions.assertTrue(foundWelcomeText, "Welcome text not found");
    }

    @BrowserTest
    public void navigationElementsArePresent() {
        // Check that navigation/drawer toggle is present (hamburger menu)
        Assertions.assertTrue($("vaadin-drawer-toggle").exists(), "Navigation drawer toggle should be present");

        // Check that the app title/logo is present in navbar
        Assertions.assertTrue($("h1").exists(), "App title should be present in navbar");
    }

    @BrowserTest
    public void pageTitleIsCorrect() {
        String title = getDriver().getTitle();
        Assertions.assertEquals("Dashboard | CL Booster", title, "Page title should match expected Dashboard title");
    }
}
