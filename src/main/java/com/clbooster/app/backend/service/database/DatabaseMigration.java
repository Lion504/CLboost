package com.clbooster.app.backend.service.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigration {

    public static void runMigration() {
        System.out.println("Running database migration for localization...");

        String[] createLocaleTable = { "CREATE TABLE IF NOT EXISTS locale (",
                "    locale_code CHAR(5) PRIMARY KEY COMMENT 'ISO 639-1 + country code',",
                "    language_name VARCHAR(64) NOT NULL,", "    native_name VARCHAR(64) NOT NULL,",
                "    rtl_direction BOOLEAN DEFAULT FALSE,", "    is_active BOOLEAN DEFAULT TRUE,",
                "    sort_order INT DEFAULT 0", ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci" };

        String[] createProfileTranslationTable = { "CREATE TABLE IF NOT EXISTS profile_translation (",
                "    profile_pin INT NOT NULL,", "    locale_code CHAR(5) NOT NULL,", "    experience_level TEXT,",
                "    tools TEXT,", "    skills TEXT,", "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,",
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,",
                "    PRIMARY KEY (profile_pin, locale_code),",
                "    FOREIGN KEY (profile_pin) REFERENCES profile(Pin) ON DELETE CASCADE,",
                "    FOREIGN KEY (locale_code) REFERENCES locale(locale_code) ON DELETE RESTRICT,",
                "    INDEX idx_profile_locale (profile_pin, locale_code)",
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci" };

        String[] createSystemMessageTranslationTable = { "CREATE TABLE IF NOT EXISTS system_message_translation (",
                "    message_key VARCHAR(128) NOT NULL,", "    locale_code CHAR(5) NOT NULL,",
                "    message_content TEXT NOT NULL,",
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,",
                "    PRIMARY KEY (message_key, locale_code),",
                "    FOREIGN KEY (locale_code) REFERENCES locale(locale_code) ON DELETE RESTRICT",
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci" };

        String[] insertLocales = {
                "INSERT IGNORE INTO locale (locale_code, language_name, native_name, rtl_direction, sort_order) VALUES",
                "('en_US', 'English', 'English', FALSE, 10),", "('fi_FI', 'Finnish', 'Suomi', FALSE, 20),",
                "('pt_BR', 'Portuguese', 'Português', FALSE, 30),", "('fa_IR', 'Persian', 'فارسی', TRUE, 40),",
                "('zh_CN', 'Chinese', '中文', FALSE, 50),", "('ur_PK', 'Urdu', 'اردو', TRUE, 60)" };

        String migrateProfileData = "INSERT IGNORE INTO profile_translation (profile_pin, locale_code, experience_level, tools, skills) "
                + "SELECT Pin, 'en_US', Experience_Level, Tools, Skills FROM profile";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Disable foreign key checks
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            }

            // Create locale table
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(String.join("\n", createLocaleTable));
                System.out.println("✓ Locale table created/verified");
            }

            // Insert locale data
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(String.join("\n", insertLocales));
                System.out.println("✓ Locale data inserted");
            }

            // Create profile_translation table
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(String.join("\n", createProfileTranslationTable));
                System.out.println("✓ Profile translation table created/verified");
            }

            // Create system_message_translation table
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(String.join("\n", createSystemMessageTranslationTable));
                System.out.println("✓ System message translation table created/verified");
            }

            // Migrate existing profile data
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(migrateProfileData);
                System.out.println("✓ Profile data migrated to translation table");
            }

            // Re-enable foreign key checks
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            }

            System.out.println("✓ Database migration completed successfully!");

        } catch (SQLException e) {
            System.err.println("Error during database migration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        runMigration();
    }
}