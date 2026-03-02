package com.clbooster.app.backend.service.settings;

public class Settings {
    private int userPin;
    
    // Appearance
    private String theme; // "light", "dark", "system"
    
    // Language
    private String language; // "English", "Finnish", etc.
    
    // Notifications
    private boolean emailNotifications;
    private boolean pushNotifications;
    private boolean productUpdates;
    private boolean marketing;
    
    // Privacy
    private boolean storeInCloud;
    private boolean allowAiTraining;
    private boolean shareUsageData;
    
    public Settings() {
        // Default values
        this.theme = "system";
        this.language = "English";
        this.emailNotifications = true;
        this.pushNotifications = false;
        this.productUpdates = true;
        this.marketing = false;
        this.storeInCloud = true;
        this.allowAiTraining = false;
        this.shareUsageData = true;
    }
    
    public Settings(int userPin) {
        this();
        this.userPin = userPin;
    }
    
    // Getters and Setters
    public int getUserPin() { return userPin; }
    public void setUserPin(int userPin) { this.userPin = userPin; }
    
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }
    
    public boolean isPushNotifications() { return pushNotifications; }
    public void setPushNotifications(boolean pushNotifications) { this.pushNotifications = pushNotifications; }
    
    public boolean isProductUpdates() { return productUpdates; }
    public void setProductUpdates(boolean productUpdates) { this.productUpdates = productUpdates; }
    
    public boolean isMarketing() { return marketing; }
    public void setMarketing(boolean marketing) { this.marketing = marketing; }
    
    public boolean isStoreInCloud() { return storeInCloud; }
    public void setStoreInCloud(boolean storeInCloud) { this.storeInCloud = storeInCloud; }
    
    public boolean isAllowAiTraining() { return allowAiTraining; }
    public void setAllowAiTraining(boolean allowAiTraining) { this.allowAiTraining = allowAiTraining; }
    
    public boolean isShareUsageData() { return shareUsageData; }
    public void setShareUsageData(boolean shareUsageData) { this.shareUsageData = shareUsageData; }
}
