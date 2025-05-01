package com.utc2.domainstore.view;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigManager {
    private static ConfigManager instance;
    private final Properties settings;
    private List<String> languages;
    private DateTimeFormatter dateTimeFormatter;
    private NumberFormat numberFormatter;

    private ConfigManager() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        languages = new ArrayList<>(List.of("Tiếng việt", "English"));
        settings = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/properties/settings.properties")) {
            if (input != null) {
                settings.load(input);
                numberFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
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

    public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public void setNumberFormatter(NumberFormat formatter) {
        this.numberFormatter = formatter;
    }

    public NumberFormat getNumberFormatter() {
        return numberFormatter;
    }

}