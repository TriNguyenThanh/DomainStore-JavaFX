package com.utc2.domainstore.view;

import com.utc2.domainstore.utils.JSONReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class ConfigManager {
    private static ConfigManager instance;
    private final Properties settings;
    private Map<String, Locale> languageMap = new HashMap<>();
    private Map<String, Integer> rateMap = new HashMap<>();
    private DateTimeFormatter dateTimeFormatter;
    private NumberFormat numberFormatter;

    private ConfigManager() {
        settings = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/properties/settings.properties");
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {

            settings.load(reader);
            numberFormatter = NumberFormat.getCurrencyInstance(getLocale());
            dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());
            loadLanguage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadLanguage() {
        JSONObject jsonObject = JSONReader.loadJSON("/json/language.json");
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray("languages");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject languageObject = jsonArray.getJSONObject(i);
                String name = languageObject.getString("name");
                String code = languageObject.getString("code");
                String localeName = languageObject.getString("locale");
                Integer rate = languageObject.getInt("rate");

                languageMap.put(name, new Locale(code, localeName));
                rateMap.put(name, rate);
            }
        }
    }

    public String getSetting(String key, String defaultValue) {
        return settings.getProperty(key, defaultValue);
    }

    public void updateSetting(String key, String value) {
        settings.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(getClass().getResource("/properties/settings.properties").getPath())) {
            settings.store(output, "Updated settings");
            System.out.println("Updated setting: " + key + " = " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResourceBundle getLanguageBundle() {
        String lang = getSetting("locale", "vi_VN");
        String languageProperty = String.format("properties.%s", lang);
        return ResourceBundle.getBundle(languageProperty, getLocale());
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

    public Locale getLocale() {
        String lang = settings.getProperty("locale", "vi_VN");
        return new Locale(lang.substring(0, lang.lastIndexOf('_')), lang.substring(lang.lastIndexOf('_') + 1));
    }

    public List<String> getLanguages() {
        return new ArrayList<>(languageMap.keySet());
    }

    public Locale getLanguageLocale(String name) {
        return languageMap.get(name);
    }

    public Integer getRate(String name) {
        return rateMap.get(name);
    }
}