package com.utc2.domainstore.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConfigManager {
    private static ConfigManager instance;
    private final Properties settings;

    private ConfigManager() {
        settings = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/properties/settings.properties")) {
            if (input != null) {
                settings.load(input);
            } else {
                throw new IOException("No found settings.properties!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public String getSetting(String key, String defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    public ResourceBundle getLanguageBundle() {
        String lang = getSetting("language", "vi");
        String country = getSetting("country", "VN");
        String languageProperty = String.format("properties.%s_%s", lang, country);
        Locale locale = new Locale(lang, country);
        return ResourceBundle.getBundle(languageProperty, locale);
    }
}
