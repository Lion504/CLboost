package com.clbooster.app.views;

import com.clbooster.aiservice.AIService;
import com.clbooster.aiservice.Exporter;
import com.clbooster.aiservice.Parser;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.Profile;
import com.clbooster.app.backend.service.profile.ProfileService;
import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.backend.service.profile.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CLgenerator_CLITest {

    private AuthenticationService authService;
    private ProfileService profileService;
    private UserDAO userDAO;
    private AIService aiService;
    private Parser parser;
    private Exporter exporter;

    @BeforeEach
    void setUp() throws Exception {
        authService = Mockito.mock(AuthenticationService.class);
        profileService = Mockito.mock(ProfileService.class);
        userDAO = Mockito.mock(UserDAO.class);
        aiService = Mockito.mock(AIService.class);
        parser = Mockito.mock(Parser.class);
        exporter = Mockito.mock(Exporter.class);

        setStaticField("authService", authService);
        setStaticField("profileService", profileService);
        setStaticField("userDAO", userDAO);
        setStaticField("aiService", aiService);
        setStaticField("parser", parser);
        setStaticField("exporter", exporter);
    }

    @Test
    void showMainMenu_handlesExitAndInvalidOptions() throws Exception {
        setScannerInput("3\n");
        boolean shouldContinue = invokeStatic("showMainMenu", Boolean.class);
        assertFalse(shouldContinue);

        setScannerInput("x\n");
        shouldContinue = invokeStatic("showMainMenu", Boolean.class);
        assertTrue(shouldContinue);
    }

    @Test
    void showMainMenu_loginAndRegisterBranchesInvokeHandlers() throws Exception {
        when(authService.isLoggedIn()).thenReturn(true);

        setScannerInput("1\nuser\npass\n");
        boolean shouldContinue = invokeStatic("showMainMenu", Boolean.class);
        assertTrue(shouldContinue);
        verify(authService).login("user", "pass");

        setScannerInput("2\nmail@test.com\nnewuser\nSecret123!\nSecret123!\nFirst\nLast\n");
        when(authService.register(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        shouldContinue = invokeStatic("showMainMenu", Boolean.class);
        assertTrue(shouldContinue);
        verify(authService).register("mail@test.com", "newuser", "Secret123!", "First", "Last");
    }

    @Test
    void showUserMenu_logoutAndExitBranchesWork() throws Exception {
        setScannerInput("3\n");
        boolean shouldContinue = invokeStatic("showUserMenu", Boolean.class);
        assertTrue(shouldContinue);
        verify(authService).logout();

        setScannerInput("4\n");
        shouldContinue = invokeStatic("showUserMenu", Boolean.class);
        assertFalse(shouldContinue);
    }

    @Test
    void handleCoverLetterGeneration_handlesNotLoggedAndMissingProfile() throws Exception {
        when(authService.getCurrentUserPin()).thenReturn(-1);
        invokeStaticVoid("handleCoverLetterGeneration");
        verify(profileService, never()).getProfile(anyInt(), any(java.util.Locale.class));

        when(authService.getCurrentUserPin()).thenReturn(10);
        when(profileService.getProfile(eq(10), any(java.util.Locale.class))).thenReturn(null);
        invokeStaticVoid("handleCoverLetterGeneration");
        verify(profileService).getProfile(eq(10), any(java.util.Locale.class));
        verify(parser, never()).parseFileToJson(anyString());
    }

    @Test
    void handleCoverLetterGeneration_successfullyParsesGeneratesAndExports() throws Exception {
        Profile profile = new Profile(77, "Senior", "Java", "Spring", "https://portfolio", "me@mail.com");
        when(authService.getCurrentUserPin()).thenReturn(77);
        when(profileService.getProfile(eq(77), any(java.util.Locale.class))).thenReturn(profile);
        when(parser.parseFileToJson(anyString())).thenReturn("resume text");
        when(aiService.generateCoverLetter(anyString(), anyString())).thenReturn("cover letter body");

        setScannerInput("/tmp/resume.pdf\njob line 1\njob line 2\n\noutFile\nn\n");
        invokeStaticVoid("handleCoverLetterGeneration");

        verify(parser).parseFileToJson("/tmp/resume.pdf");
        verify(aiService).generateCoverLetter(anyString(), anyString());
        verify(exporter).saveAsDoc("cover letter body", "outFile.docx");
    }

    @Test
    void handleCoverLetterGeneration_handlesGenerationException() throws Exception {
        Profile profile = new Profile(77, "Senior", "Java", "Spring", "https://portfolio", "me@mail.com");
        when(authService.getCurrentUserPin()).thenReturn(77);
        when(profileService.getProfile(eq(77), any(java.util.Locale.class))).thenReturn(profile);
        when(parser.parseFileToJson(anyString())).thenReturn("resume text");
        when(aiService.generateCoverLetter(anyString(), anyString())).thenThrow(new RuntimeException("fail"));

        setScannerInput("/tmp/resume.pdf\njob line 1\n\noutFile\nn\n");
        invokeStaticVoid("handleCoverLetterGeneration");

        verify(exporter, never()).saveAsDoc(anyString(), anyString());
    }

    @Test
    void handleEditProfile_updatesWhenConfirmed() throws Exception {
        Profile profile = new Profile(12, "Mid", "Java", "Spring", "https://old", "old@mail.com");
        when(authService.getCurrentUserPin()).thenReturn(12);
        when(profileService.getProfile(eq(12), any(java.util.Locale.class))).thenReturn(profile);

        setScannerInput("Senior\nJava,SQL\nSpring,Cloud\nhttps://new\nnew@mail.com\ny\n");
        invokeStaticVoid("handleEditProfile");

        verify(profileService).updateProfile(eq(12), eq(""), eq(""), eq("Senior"), eq("Java,SQL"), eq("Spring,Cloud"), eq("https://new"), eq("new@mail.com"), any(java.util.Locale.class));
    }

    @Test
    void handleEditProfile_coversNotLoggedMissingProfileAndDiscard() throws Exception {
        when(authService.getCurrentUserPin()).thenReturn(-1);
        invokeStaticVoid("handleEditProfile");
        verify(profileService, never()).getProfile(anyInt(), any(java.util.Locale.class));

        when(authService.getCurrentUserPin()).thenReturn(12);
        when(profileService.getProfile(eq(12), any(java.util.Locale.class))).thenReturn(null);
        invokeStaticVoid("handleEditProfile");
        verify(profileService).getProfile(eq(12), any(java.util.Locale.class));

        Profile profile = new Profile(12, "Mid", "Java", "Spring", "https://old", "old@mail.com");
        when(profileService.getProfile(eq(12), any(java.util.Locale.class))).thenReturn(profile);
        setScannerInput("\n\n\n\n\nn\n");
        invokeStaticVoid("handleEditProfile");
        verify(profileService, never()).updateProfile(anyInt(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(java.util.Locale.class));
    }

    @Test
    void handleProfileMenu_andViewProfileDetails_branchesCovered() throws Exception {
        when(authService.getCurrentUserPin()).thenReturn(-1);
        boolean keepRunning = invokeStatic("handleProfileMenu", Boolean.class);
        assertTrue(keepRunning);

        when(authService.getCurrentUserPin()).thenReturn(12);
        Profile profile = new Profile(12, "Mid", "Java", "Spring", "https://old", "old@mail.com");
        when(profileService.getProfile(eq(12), any(java.util.Locale.class))).thenReturn(profile);

        setScannerInput("2\ny\n4\n");
        keepRunning = invokeStatic("handleProfileMenu", Boolean.class);
        assertTrue(keepRunning);
        verify(profileService, atLeastOnce()).displayProfile(12);
        verify(profileService, atLeastOnce()).getProfile(eq(12), any(java.util.Locale.class));

        when(profileService.getProfile(eq(12), any(java.util.Locale.class))).thenReturn(null);
        setScannerInput("2\n4\n");
        keepRunning = invokeStatic("handleProfileMenu", Boolean.class);
        assertTrue(keepRunning);
    }

    @Test
    void handleDeleteAccount_notLoggedCancelledAndDeleteFailure() throws Exception {
        when(authService.getCurrentUserPin()).thenReturn(-1);
        boolean deleted = invokeStatic("handleDeleteAccount", Boolean.class);
        assertFalse(deleted);

        User current = new User("a@b.com", "tester", "Password1!x", "A", "B");
        current.setPin(5);
        when(authService.getCurrentUserPin()).thenReturn(5);
        when(authService.getCurrentUser()).thenReturn(current);

        setScannerInput("no\n");
        deleted = invokeStatic("handleDeleteAccount", Boolean.class);
        assertFalse(deleted);

        when(userDAO.deleteUser(current)).thenReturn(false);
        setScannerInput("yes\ntester\n");
        deleted = invokeStatic("handleDeleteAccount", Boolean.class);
        assertFalse(deleted);
    }

    @Test
    void handleDeleteAccount_checksConfirmationAndDeletes() throws Exception {
        User current = new User("a@b.com", "tester", "Password1!x", "A", "B");
        current.setPin(5);

        when(authService.getCurrentUserPin()).thenReturn(5);
        when(authService.getCurrentUser()).thenReturn(current);
        when(userDAO.deleteUser(current)).thenReturn(true);

        setScannerInput("yes\ntester\n");
        boolean deleted = invokeStatic("handleDeleteAccount", Boolean.class);

        assertTrue(deleted);
        verify(userDAO).deleteUser(current);
        verify(authService).logout();

        setScannerInput("yes\nwrong\n");
        deleted = invokeStatic("handleDeleteAccount", Boolean.class);
        assertFalse(deleted);
    }

    private void setScannerInput(String input) throws Exception {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        setStaticField("scanner", scanner);
    }

    private void setStaticField(String fieldName, Object value) throws Exception {
        Field field = CLgenerator_CLI.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T invokeStatic(String methodName, Class<T> type) throws Exception {
        Method method = CLgenerator_CLI.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return (T) method.invoke(null);
    }

    private void invokeStaticVoid(String methodName) throws Exception {
        Method method = CLgenerator_CLI.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(null);
    }
}
