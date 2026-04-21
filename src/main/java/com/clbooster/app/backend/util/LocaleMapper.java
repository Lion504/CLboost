package com.clbooster.app.backend.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocaleMapper {
    private LocaleMapper() {
        // Hide implicit public constructor
    }
    private static final String EN_US = "en_US";

    private static final Map<String, String> APP_TO_DB = new HashMap<>();

    static {
        APP_TO_DB.put("en", EN_US);
        APP_TO_DB.put("fi", "fi_FI");
        APP_TO_DB.put("pt", "pt_BR");
        APP_TO_DB.put("fa", "fa_IR");
        APP_TO_DB.put("zh", "zh_CN");
        APP_TO_DB.put("ur", "ur_PK");
    }

    public static String getDbCode(Locale locale) {
        if (locale == null)
            return EN_US;
        String lang = locale.getLanguage();
        return APP_TO_DB.getOrDefault(lang, EN_US);
    }
}
