-- =============================================
-- CLboost Database Localization Migration v1
-- =============================================
-- Charset: utf8mb4_unicode_ci
-- Date: 2026-04-07

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Convert all existing tables to proper utf8mb4 charset
ALTER DATABASE CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

ALTER TABLE identification CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE profile CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE cover_letter CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE settings CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. Create locale reference table
CREATE TABLE locale (
    locale_code CHAR(5) PRIMARY KEY COMMENT 'ISO 639-1 + country code',
    language_name VARCHAR(64) NOT NULL,
    native_name VARCHAR(64) NOT NULL,
    rtl_direction BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Populate supported locales
INSERT INTO locale (locale_code, language_name, native_name, rtl_direction, sort_order) VALUES
('en_US', 'English', 'English', FALSE, 10),
('fi_FI', 'Finnish', 'Suomi', FALSE, 20),
('pt_BR', 'Portuguese', 'Português', FALSE, 30),
('fa_IR', 'Persian', 'فارسی', TRUE, 40),
('zh_CN', 'Chinese', '中文', FALSE, 50),
('ur_PK', 'Urdu', 'اردو', TRUE, 60);

-- 3. Create profile translation table
CREATE TABLE profile_translation (
    profile_pin INT NOT NULL,
    locale_code CHAR(5) NOT NULL,
    experience_level TEXT,
    tools TEXT,
    skills TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (profile_pin, locale_code),
    FOREIGN KEY (profile_pin) REFERENCES profile(Pin) ON DELETE CASCADE,
    FOREIGN KEY (locale_code) REFERENCES locale(locale_code) ON DELETE RESTRICT,
    INDEX idx_profile_locale (profile_pin, locale_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Create system message translation table
CREATE TABLE system_message_translation (
    message_key VARCHAR(128) NOT NULL,
    locale_code CHAR(5) NOT NULL,
    message_content TEXT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (message_key, locale_code),
    FOREIGN KEY (locale_code) REFERENCES locale(locale_code) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Migrate existing profile data to default locale
INSERT IGNORE INTO profile_translation (profile_pin, locale_code, experience_level, tools, skills)
SELECT Pin, 'en_US', Experience_Level, Tools, Skills FROM profile;

SET FOREIGN_KEY_CHECKS = 1;
