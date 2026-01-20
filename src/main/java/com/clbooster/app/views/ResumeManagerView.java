package com.clbooster.app.views;

import com.clbooster.app.backend.service.ResumeService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Resume manager view for uploading and managing resumes.
 */
@Route(value = "resumes", layout = MainLayout.class)
@PageTitle("Resume Manager | CL Booster")
public class ResumeManagerView extends VerticalLayout {

    private final ResumeService resumeService;

    public ResumeManagerView(ResumeService resumeService) {
        this.resumeService = resumeService;

        add(new H1("Resume Manager"));
        // TODO: Implement resume upload and management functionality
    }
}
