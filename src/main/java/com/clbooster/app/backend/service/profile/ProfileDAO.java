package com.clbooster.app.backend.service.profile;

import com.clbooster.app.backend.dao.LocalizableDAO;
import com.clbooster.app.backend.service.database.DatabaseConnection;
import com.clbooster.app.backend.util.LocaleFallbackResolver;
import com.clbooster.app.backend.util.LocaleMapper;
import com.clbooster.app.backend.util.Utf8Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileDAO implements LocalizableDAO<Profile> {

    private static final Logger log = LoggerFactory.getLogger(ProfileDAO.class);

    public Profile getProfileByPin(int pin) {
        String sql = "SELECT Pin, Experience_Level, Tools, Skills, Link, Profile_Email, CV_Last_Updated FROM profile WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Profile profile = new Profile();
                profile.setPin(rs.getInt("Pin"));
                profile.setExperienceLevel(rs.getString("Experience_Level"));
                profile.setTools(rs.getString("Tools"));
                profile.setSkills(rs.getString("Skills"));
                profile.setLink(rs.getString("Link"));
                profile.setProfileEmail(rs.getString("Profile_Email"));
                profile.setCvLastUpdated(rs.getTimestamp("CV_Last_Updated"));
                return profile;
            }
            return null;

        } catch (SQLException e) {
            log.error("Failed to load profile by PIN {}", pin, e);
            return null;
        }
    }

    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profile SET Experience_Level = ?, Tools = ?, Skills = ?, Link = ?, Profile_Email = ? WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, profile.getExperienceLevel());
            pstmt.setString(2, profile.getTools());
            pstmt.setString(3, profile.getSkills());
            pstmt.setString(4, profile.getLink());
            pstmt.setString(5, profile.getProfileEmail());
            pstmt.setInt(6, profile.getPin());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            log.error("Failed to update profile for PIN {}", profile.getPin(), e);
            return false;
        }
    }

    // CV last updated timestamp
    public boolean updateCVTimestamp(int pin) {
        String sql = "UPDATE profile SET CV_Last_Updated = CURRENT_TIMESTAMP WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            log.error("Failed to update CV timestamp for PIN {}", pin, e);
            return false;
        }
    }

    public boolean profileExists(int pin) {
        String sql = "SELECT Pin FROM profile WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            log.error("Failed to check profile existence for PIN {}", pin, e);
            return false;
        }
    }

    @Override
    public Profile getById(int pin, Locale locale) {
        Profile base = getProfileByPin(pin);
        if (base == null || locale == null)
            return base;

        String sql = "SELECT experience_level, tools, skills FROM profile_translation WHERE profile_pin = ? AND locale_code = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            pstmt.setString(2, LocaleMapper.getDbCode(locale));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                base.setExperienceLevel(rs.getString("experience_level"));
                base.setTools(rs.getString("tools"));
                base.setSkills(rs.getString("skills"));
            }
            return base;

        } catch (SQLException e) {
            log.error("Failed to load translated profile for PIN {} and locale {}", pin, locale, e);
            return base;
        }
    }

    @Override
    public Profile getByIdWithFallback(int pin, Locale preferred, Locale fallback) {
        for (Locale locale : LocaleFallbackResolver.getFallbackChain(preferred)) {
            if (hasTranslation(pin, locale)) {
                return getById(pin, locale);
            }
        }
        return getProfileByPin(pin);
    }

    @Override
    public void saveTranslation(Profile profile, Locale locale) {
        if (!Utf8Validator.isValidUtf8(profile.getSkills()) || !Utf8Validator.isValidUtf8(profile.getTools())
                || !Utf8Validator.isValidUtf8(profile.getExperienceLevel())) {
            throw new IllegalArgumentException("Invalid UTF-8 sequence detected");
        }

        String sql = "INSERT INTO profile_translation (profile_pin, locale_code, experience_level, tools, skills) "
                + "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE experience_level = ?, tools = ?, skills = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String code = LocaleMapper.getDbCode(locale);
            pstmt.setInt(1, profile.getPin());
            pstmt.setString(2, code);
            pstmt.setString(3, Utf8Validator.sanitize(profile.getExperienceLevel()));
            pstmt.setString(4, Utf8Validator.sanitize(profile.getTools()));
            pstmt.setString(5, Utf8Validator.sanitize(profile.getSkills()));
            pstmt.setString(6, Utf8Validator.sanitize(profile.getExperienceLevel()));
            pstmt.setString(7, Utf8Validator.sanitize(profile.getTools()));
            pstmt.setString(8, Utf8Validator.sanitize(profile.getSkills()));

            pstmt.executeUpdate();

            // Also update the base profile for core non-localizable fields (Link, Email)
            updateProfile(profile);

        } catch (SQLException e) {
            log.error("Failed to save profile translation for PIN {} and locale {}", profile.getPin(), locale, e);
        }
    }

    @Override
    public List<Locale> getAvailableLocales(int entityId) {
        List<Locale> locales = new ArrayList<>();
        String sql = "SELECT locale_code FROM profile_translation WHERE profile_pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, entityId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                locales.add(Locale.forLanguageTag(rs.getString("locale_code").replace('_', '-')));
            }
            return locales;

        } catch (SQLException e) {
            log.error("Failed to load available profile locales for PIN {}", entityId, e);
            return locales;
        }
    }

    @Override
    public boolean hasTranslation(int id, Locale locale) {
        String sql = "SELECT 1 FROM profile_translation WHERE profile_pin = ? AND locale_code = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.setString(2, LocaleMapper.getDbCode(locale));
            return pstmt.executeQuery().next();

        } catch (SQLException e) {
            log.error("Failed to check profile translation for PIN {} and locale {}", id, locale, e);
            return false;
        }
    }
}