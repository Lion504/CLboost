package com.clbooster.app.backend.dao;

import java.util.List;
import java.util.Locale;

public interface LocalizableDAO<T> {

    T getById(int id, Locale locale);

    T getByIdWithFallback(int id, Locale preferred, Locale fallback);

    void saveTranslation(T entity, Locale locale);

    List<Locale> getAvailableLocales(int entityId);

    boolean hasTranslation(int id, Locale locale);
}
