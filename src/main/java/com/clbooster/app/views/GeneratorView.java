package com.clbooster.app.views;

import com.clbooster.app.backend.service.ai.AIService;
import com.clbooster.app.backend.service.document.DocumentService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Cover letter generator view - the core workflow.
 */
@Route(value = "generator", layout = MainLayout.class)
@PageTitle("Generator | CL Booster")
public class GeneratorView extends VerticalLayout {

    private final AIService aiService;
    private final DocumentService documentService;

    public GeneratorView(AIService aiService, DocumentService documentService) {
        this.aiService = aiService;
        this.documentService = documentService;

        add(new H1("Cover Letter Generator"));
        // TODO: Implement AI-powered cover letter generation workflow
    }
}
