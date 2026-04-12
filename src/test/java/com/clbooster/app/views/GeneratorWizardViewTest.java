package com.clbooster.app.views;

import com.clbooster.aiservice.AIService;
import com.clbooster.aiservice.Exporter;
import com.clbooster.aiservice.Parser;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.Profile;
import com.clbooster.app.backend.service.profile.ProfileDAO;
import com.clbooster.app.backend.service.profile.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.Command;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GeneratorWizardViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesAndStartsAtStep1() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        GeneratorWizardView view = new GeneratorWizardView(aiService);

        assertNotNull(view);
        assertEquals(1, getIntField(view, "currentStep"));
        assertNotNull(getField(view, "stepContentContainer", Div.class));
        assertNotNull(getField(view, "stepIndicator", HorizontalLayout.class));
        assertNotNull(getField(view, "nextButton", Button.class));
        assertNotNull(getField(view, "backButton", Button.class));
        assertNotNull(getField(view, "step1JobTitleField", TextField.class));
    }

    @Test
    void stepRenderers_andNavigationState_workAcrossSteps() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        GeneratorWizardView view = new GeneratorWizardView(aiService);

        VerticalLayout step2 = invoke(view, "createStep2Qualifications", VerticalLayout.class);
        VerticalLayout step3 = invoke(view, "createStep3AICustomization", VerticalLayout.class);
        VerticalLayout step4 = invoke(view, "createStep4Review", VerticalLayout.class);

        assertNotNull(step2);
        assertNotNull(step3);
        assertNotNull(step4);

        invokeVoid(view, "showStep", new Class<?>[] { int.class }, new Object[] { 2 });
        assertEquals(2, getIntField(view, "currentStep"));

        setIntField(view, "currentStep", 5);
        invokeNoArgs(view, "updateNavigationButtons");

        Button next = getField(view, "nextButton", Button.class);
        Button save = getField(view, "saveButton", Button.class);
        Button back = getField(view, "backButton", Button.class);

        assertFalse(next.isVisible());
        assertTrue(save.isVisible());
        assertTrue(back.isVisible());
    }

    @Test
    void validateStep1Fields_andHandleNext_enforceRequiredDataAndTone() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        GeneratorWizardView view = new GeneratorWizardView(aiService);

        TextField job = getField(view, "step1JobTitleField", TextField.class);
        TextField company = getField(view, "step1CompanyField", TextField.class);
        TextArea description = getField(view, "step1DescField", TextArea.class);

        job.setValue("");
        company.setValue("");
        description.setValue("");

        boolean invalid = invoke(view, "validateStep1Fields", Boolean.class);
        assertFalse(invalid);
        assertTrue(job.isInvalid());
        assertTrue(company.isInvalid());
        assertTrue(description.isInvalid());

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            job.setValue("Software Engineer");
            company.setValue("Acme");
            description.setValue("Build features and APIs");

            invokeNoArgs(view, "handleNext");
            assertEquals(2, getIntField(view, "currentStep"));

            invokeNoArgs(view, "handleNext");
            assertEquals(3, getIntField(view, "currentStep"));

            invokeNoArgs(view, "handleNext");
            assertEquals(3, getIntField(view, "currentStep"));

            setStringField(view, "selectedTone", "creative");
            invokeNoArgs(view, "handleNext");
            assertEquals(4, getIntField(view, "currentStep"));
        }
    }

    @Test
    void skillsExtraction_selectionAndLoadResumeSkills_work() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        GeneratorWizardView view = new GeneratorWizardView(aiService);

        @SuppressWarnings("unchecked")
        Set<String> skills = invoke(view, "extractSkillsFromText", Set.class, new Class<?>[] { String.class },
                new Object[] { "Experienced with React, Node.js, UI Design and AWS in Agile teams." });

        assertTrue(skills.contains("React"));
        assertTrue(skills.contains("Node.js"));
        assertTrue(skills.contains("UI Design"));
        assertTrue(skills.contains("AWS"));

        Button skillButton = invoke(view, "createSkillButton", Button.class, new Class<?>[] { String.class },
                new Object[] { "React" });
        skillButton.click();
        @SuppressWarnings("unchecked")
        Set<String> selected = getField(view, "selectedSkills", Set.class);
        assertTrue(selected.contains("React"));
        skillButton.click();
        assertFalse(selected.contains("React"));

        try (MockedConstruction<Parser> parserMock = Mockito.mockConstruction(Parser.class,
                (mock, context) -> when(mock.parseFileToJson(anyString())).thenReturn("React AWS"));
                MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            invokeVoid(view, "loadResumeSkills", new Class<?>[] { File.class },
                    new Object[] { new File("dummy_resume.pdf") });

            assertTrue(selected.contains("React"));
            assertTrue(selected.contains("AWS"));
            assertEquals(1, parserMock.constructed().size());
        }
    }

    @Test
    void generationUtilities_fallbackWrapSanitizeAndPdf() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        when(aiService.generateCoverLetter(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("AI unavailable"));

        GeneratorWizardView view = new GeneratorWizardView(aiService);
        setStringField(view, "jobTitle", "Developer");
        setStringField(view, "companyName", "Acme");
        setStringField(view, "jobDescription", "Build web apps");
        setStringField(view, "selectedTone", "professional");
        setIntField(view, "capturedUserPin", -1);
        setStringField(view, "capturedUserName", "Test User");

        String generated = invoke(view, "generateCoverLetterText", String.class);
        assertTrue(generated.contains("Dear Hiring Manager"));

        TextArea editor = new TextArea();
        editor.setValue("content");
        setField(view, "editorTextArea", editor);

        invokeVoid(view, "wrapEditorContent", new Class<?>[] { String.class, String.class },
                new Object[] { "**", "**" });
        assertEquals("**content**", editor.getValue());

        invokeVoid(view, "wrapEditorContent", new Class<?>[] { String.class, String.class },
                new Object[] { "**", "**" });
        assertEquals("content", editor.getValue());

        String sanitized = invoke(view, "sanitizeEditorFilename", String.class, new Class<?>[] { String.class },
                new Object[] { "  ACME / Senior Dev!!!  " });
        assertEquals("ACME_Senior_Dev", sanitized);

        byte[] pdf = invoke(view, "generateSimplePdf", byte[].class, new Class<?>[] { String.class },
                new Object[] { "Line 1\nLine 2" });
        assertNotNull(pdf);
        assertTrue(pdf.length > 20);
        assertEquals("%PDF-1.4", new String(pdf, 0, 8));
    }

    @Test
    void saveGeneratedCoverLetter_usesCurrentUserAndExporter() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        GeneratorWizardView view = new GeneratorWizardView(aiService);

        User user = new User("a@b.com", "tester", "Password1!x", "T", "U");
        user.setPin(7777);

        try (MockedConstruction<AuthenticationService> authMock = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user));
                MockedConstruction<Exporter> exporterMock = Mockito.mockConstruction(Exporter.class,
                        (mock, context) -> Mockito.doNothing().when(mock).saveAsDoc(anyString(), anyString()))) {

            String path = invoke(view, "saveGeneratedCoverLetter", String.class, new Class<?>[] { String.class },
                    new Object[] { "Generated text" });

            assertNotNull(path);
            assertTrue(path.contains("uploads"));
            assertEquals(1, authMock.constructed().size());
            assertEquals(1, exporterMock.constructed().size());
            verify(exporterMock.constructed().get(0)).saveAsDoc(eq("Generated text"), anyString());
        }
    }

    @Test
    void loadUserResumeText_returnsNullWhenNoFilesForPin() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        GeneratorWizardView view = new GeneratorWizardView(aiService);

        String resume = invoke(view, "loadUserResumeText", String.class, new Class<?>[] { int.class },
                new Object[] { 999999 });

        assertNull(resume);
    }

    @Test
    void createStep2Qualifications_coversSavedResumeSingleAndMultipleBranches() throws Exception {
        Path resumeDir = Path.of("uploads", "resumes");
        Files.createDirectories(resumeDir);

        Path single = resumeDir.resolve("9001_1700000000000_resumeA.pdf");
        Path multiA = resumeDir.resolve("9002_1700000000001_resumeA.pdf");
        Path multiB = resumeDir.resolve("9002_1700000000002_resumeB.docx");
        Files.writeString(single, "single", StandardCharsets.UTF_8);
        Files.writeString(multiA, "a", StandardCharsets.UTF_8);
        Files.writeString(multiB, "b", StandardCharsets.UTF_8);

        AIService aiService = Mockito.mock(AIService.class);

        User singleUser = new User("a@b.com", "u1", "Password1!x", "A", "B");
        singleUser.setPin(9001);
        try (MockedConstruction<AuthenticationService> authMock = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> Mockito.when(mock.getCurrentUser()).thenReturn(singleUser))) {
            GeneratorWizardView view = new GeneratorWizardView(aiService);
            VerticalLayout step2 = invoke(view, "createStep2Qualifications", VerticalLayout.class);
            assertNotNull(step2);
            assertTrue(findComponents(step2, Select.class).isEmpty());
            assertTrue(authMock.constructed().size() >= 1);
        }

        User multiUser = new User("c@d.com", "u2", "Password1!x", "C", "D");
        multiUser.setPin(9002);
        try (MockedConstruction<AuthenticationService> authMock = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> Mockito.when(mock.getCurrentUser()).thenReturn(multiUser))) {
            GeneratorWizardView view = new GeneratorWizardView(aiService);
            VerticalLayout step2 = invoke(view, "createStep2Qualifications", VerticalLayout.class);
            assertNotNull(step2);
            assertFalse(findComponents(step2, Select.class).isEmpty());
            assertTrue(authMock.constructed().size() >= 1);
        }

        Files.deleteIfExists(single);
        Files.deleteIfExists(multiA);
        Files.deleteIfExists(multiB);
    }

    @Test
    void createStep5Editor_coversGenerationAndToolbarActions() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        Mockito.when(aiService.generateCoverLetter(anyString(), anyString(), anyString())).thenReturn("Generated body");

        GeneratorWizardView view = new GeneratorWizardView(aiService);
        setStringField(view, "jobTitle", "Engineer");
        setStringField(view, "companyName", "Acme");
        setStringField(view, "jobDescription", "Build APIs");
        setStringField(view, "selectedTone", "professional");
        setField(view, "selectedSkills", new java.util.HashSet<>(Set.of("Java", "Spring")));

        User user = new User("a@b.com", "tester", "Password1!x", "Test", "User");
        user.setPin(55);

        UI ui = Mockito.mock(UI.class);
        Page page = Mockito.mock(Page.class);
        Mockito.when(ui.getPage()).thenReturn(page);
        Mockito.doAnswer(invocation -> {
            Command command = invocation.getArgument(0);
            command.execute();
            return null;
        }).when(ui).access(Mockito.any(Command.class));

        try (MockedConstruction<AuthenticationService> authMock = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> Mockito.when(mock.getCurrentUser()).thenReturn(user));
                MockedStatic<UI> uiMock = Mockito.mockStatic(UI.class);
                MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            uiMock.when(UI::getCurrent).thenReturn(ui);
            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            VerticalLayout editorLayout = invoke(view, "createStep5Editor", VerticalLayout.class);
            assertNotNull(editorLayout);
            TextArea editor = getField(view, "editorTextArea", TextArea.class);
            assertNotNull(editor);

            // Trigger copy, clear and regenerate buttons from toolbar.
            List<Button> buttons = findComponents(editorLayout, Button.class);
            for (int i = 2; i < buttons.size(); i++) {
                buttons.get(i).click();
            }

            assertTrue(authMock.constructed().size() >= 1);
        }
    }

    @Test
    void buildCandidateContext_returnsFallbackSummaryWhenUserNotCaptured() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        GeneratorWizardView view = new GeneratorWizardView(aiService);

        setIntField(view, "capturedUserPin", -1);
        setStringField(view, "jobTitle", "Backend Engineer");
        setStringField(view, "companyName", "Acme");
        setField(view, "selectedSkills", new java.util.HashSet<>(Set.of("Java", "Spring")));

        String context = invoke(view, "buildCandidateContext", String.class);
        assertTrue(context.contains("Experienced professional with skills in"));
        assertTrue(context.contains("Backend Engineer"));
        assertTrue(context.contains("Acme"));
    }

    @Test
    void buildCandidateContext_includesProfileAndWizardSkillsWhenNoResumeFound() throws Exception {
        Path resumeDir = Path.of("uploads", "resumes");
        Files.createDirectories(resumeDir);
        Files.deleteIfExists(resumeDir.resolve("777_1700000000000_resume.pdf"));

        AIService aiService = Mockito.mock(AIService.class);
        Profile profile = new Profile();
        profile.setExperienceLevel("Senior");
        profile.setSkills("Java, Spring");
        profile.setTools("Maven");
        profile.setLink("https://example.com");
        profile.setProfileEmail("jane@example.com");

        try (MockedConstruction<ProfileDAO> profileDaoMock = Mockito.mockConstruction(ProfileDAO.class,
                (mock, context) -> Mockito.when(mock.getProfileByPin(777)).thenReturn(profile))) {
            GeneratorWizardView view = new GeneratorWizardView(aiService);
            setIntField(view, "capturedUserPin", 777);
            setStringField(view, "capturedUserName", "Jane Doe");
            setField(view, "selectedSkills", new java.util.HashSet<>(Set.of("Java", "Spring")));

            String context = invoke(view, "buildCandidateContext", String.class);
            assertTrue(context.contains("Name: Jane Doe"));
            assertTrue(context.contains("Experience Level: Senior"));
            assertTrue(context.contains("Profile Skills: Java, Spring"));
            assertTrue(context.contains("Tools & Technologies: Maven"));
            assertTrue(context.contains("Portfolio/LinkedIn: https://example.com"));
            assertTrue(context.contains("Contact Email: jane@example.com"));
            assertTrue(context.contains("Selected Skills for this application"));
            assertEquals(1, profileDaoMock.constructed().size());
        }
    }

    @Test
    void loadResumeSkills_coversNoSkillsAndExceptionBranches() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        GeneratorWizardView view = new GeneratorWizardView(aiService);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class);
                MockedConstruction<Parser> parserNoSkills = Mockito.mockConstruction(Parser.class,
                        (mock, context) -> when(mock.parseFileToJson(anyString())).thenReturn("no matching tokens"))) {
            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            invokeVoid(view, "loadResumeSkills", new Class<?>[] { File.class },
                    new Object[] { new File("no_skills_resume.pdf") });

            assertEquals(1, parserNoSkills.constructed().size());
            notificationMock.verify(() -> Notification.show(anyString(), Mockito.anyInt(), any()),
                    Mockito.atLeastOnce());
        }

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class);
                MockedConstruction<Parser> parserFail = Mockito.mockConstruction(Parser.class,
                        (mock, context) -> when(mock.parseFileToJson(anyString()))
                                .thenThrow(new RuntimeException("parse failure")))) {
            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            invokeVoid(view, "loadResumeSkills", new Class<?>[] { File.class },
                    new Object[] { new File("broken_resume.pdf") });

            assertEquals(1, parserFail.constructed().size());
            notificationMock.verify(() -> Notification.show(anyString(), Mockito.anyInt(), any()),
                    Mockito.atLeastOnce());
        }
    }

    @Test
    void editorDownloadMethods_coverNullBlankAndDocxFailureBranches() throws Exception {
        AIService aiService = Mockito.mock(AIService.class);
        GeneratorWizardView view = new GeneratorWizardView(aiService);

        // Null editor branch: methods should return immediately.
        setField(view, "editorTextArea", null);
        invokeNoArgs(view, "downloadEditorAsDocx");
        invokeNoArgs(view, "downloadEditorAsPdf");

        TextArea editor = new TextArea();
        setField(view, "editorTextArea", editor);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            // Blank content branches.
            editor.setValue(" ");
            invokeNoArgs(view, "downloadEditorAsDocx");
            invokeNoArgs(view, "downloadEditorAsPdf");
            notificationMock.verify(() -> Notification.show(anyString(), Mockito.anyInt(), any()), Mockito.atLeast(2));
        }

        try (MockedConstruction<Exporter> exporterMock = Mockito.mockConstruction(Exporter.class,
                (mock, context) -> Mockito.doThrow(new RuntimeException("disk error")).when(mock).saveAsDoc(anyString(),
                        anyString()));
                MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            editor.setValue("Generated letter body");
            invokeNoArgs(view, "downloadEditorAsDocx");

            assertEquals(1, exporterMock.constructed().size());
            notificationMock.verify(() -> Notification.show(anyString(), Mockito.anyInt(), any()),
                    Mockito.atLeastOnce());
        }
    }

    private <T extends Component> List<T> findComponents(Component root, Class<T> type) {
        List<T> results = new ArrayList<>();
        if (type.isInstance(root)) {
            results.add(type.cast(root));
        }
        root.getChildren().forEach(child -> results.addAll(findComponents(child, type)));
        return results;
    }

    @SuppressWarnings("unchecked")
    private <T> T invoke(Object target, String methodName, Class<T> type) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return (T) method.invoke(target);
    }

    @SuppressWarnings("unchecked")
    private <T> T invoke(Object target, String methodName, Class<T> type, Class<?>[] parameterTypes, Object[] args)
            throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return (T) method.invoke(target, args);
    }

    private void invokeVoid(Object target, String methodName, Class<?>[] parameterTypes, Object[] args)
            throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        method.invoke(target, args);
    }

    private void invokeNoArgs(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }

    private int getIntField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getInt(target);
    }

    private void setIntField(Object target, String fieldName, int value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setInt(target, value);
    }

    private void setStringField(Object target, String fieldName, String value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
