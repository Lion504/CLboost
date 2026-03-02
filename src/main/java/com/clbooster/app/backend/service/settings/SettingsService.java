package com.clbooster.app.backend.service.settings;

public class SettingsService {
    private SettingsDAO settingsDAO;
    
    public SettingsService() {
        this.settingsDAO = new SettingsDAO();
    }
    
    public Settings getSettings(int userPin) {
        return settingsDAO.getSettings(userPin);
    }
    
    public boolean saveSettings(Settings settings) {
        return settingsDAO.saveSettings(settings);
    }
    
    public boolean deleteSettings(int userPin) {
        return settingsDAO.deleteSettings(userPin);
    }
}
