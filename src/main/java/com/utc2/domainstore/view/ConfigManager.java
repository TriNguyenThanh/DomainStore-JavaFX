package com.utc2.domainstore.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConfigManager {
    private static ConfigManager instance;
    private final Properties settings;
    private List<String> languages;

    private ConfigManager() {
        languages = List.of("Tiếng việt", "English");
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

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getSetting(String key, String defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    public void updateSetting(String key, String value) {
        settings.replace(key, value);
        System.out.println("Updated setting: " + key + " = " + value);
    }

    public ResourceBundle getLanguageBundle() {
        String lang = getSetting("locale", "vi_VN");
        String languageProperty = String.format("properties.%s", lang);
        Locale locale = new Locale(lang);
        return ResourceBundle.getBundle(languageProperty, locale);
    }
}