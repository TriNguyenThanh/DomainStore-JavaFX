package com.utc2.domainstore.view;

//import animatefx.animation.Shake;

import com.utc2.domainstore.App;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class SceneManager {
    private static SceneManager instance;
    private static Stage stage;

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public static void init(Stage inputStage) {
        if (stage == null) {
            stage = inputStage;
            stage.setOnCloseRequest(event -> {
                event.consume();
                Platform.exit();
            });
        }
    }

    public void switchScene(String fxmlPath) {
        if (stage == null) {
            throw new IllegalStateException("Stage has not been initialized");
        }
        try {
            ResourceBundle rb = ConfigManager.getInstance().getLanguageBundle();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath), rb);

            Scene scene = new Scene(fxmlLoader.load());

            stage.setResizable(true);
            stage.setMaximized(false);
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            throw new RuntimeException("Can't switch scene");
        }
    }

    public void setResizable(boolean resizable) {
        Platform.runLater(() -> {
            if (stage == null) {
                throw new IllegalStateException("Stage has not been initialized");
            }
            stage.setResizable(resizable);
        });
    }

    public void setTitle(String title) {
        if (stage == null) {
            throw new IllegalStateException("Stage has not been initialized");
        }
        stage.setTitle(title);
    }

    public void setIcon(String iconPath) {
        if (stage == null) {
            throw new IllegalStateException("Stage has not been initialized");
        }
        stage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
    }

    public Stage getStage() {
        return stage;
    }

    public void show() {
        if (stage != null) {
            stage.show();
//            new Shake(stage.getScene().getRoot());
        }
    }

    public void setMaximized(boolean maximized) {
        Platform.runLater(() -> {
            stage.setMaximized(maximized);
        });
    }

    public void center() {
        if (stage == null) {
            throw new IllegalStateException("Stage has not been initialized");
        }
        stage.centerOnScreen();
    }

    public Optional<ButtonType> showDialog(Alert.AlertType type, String title, String headerText, String contentText) {
        if (stage == null) {
            throw new IllegalStateException("Stage has not been initialized");
        }
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert.showAndWait();
    }

    public Image getIcon(String s, Integer width, Integer height) {
        return new Image(getClass().getResourceAsStream(s), width, height, false, true);
    }

    public void initLanguageComboBox(ComboBox<String> comboBox) {
        ResourceBundle rb = ConfigManager.getInstance().getLanguageBundle();
        comboBox.getItems().addAll(ConfigManager.getInstance().getLanguages());
        comboBox.setValue(ConfigManager.getInstance().getSetting("language", ""));

        comboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            String selectedLanguage = comboBox.getSelectionModel().getSelectedItem();
            if (selectedLanguage == null) {
                return;
            } else {
                System.out.println("Selected language.json: " + selectedLanguage);
            }
            Locale locale = ConfigManager.getInstance().getLanguageLocale(selectedLanguage);
            String localeName = locale.getLanguage() + "_" + locale.getCountry();
            ConfigManager.getInstance().updateSetting("language", selectedLanguage);
            ConfigManager.getInstance().updateSetting("locale", localeName);

            Optional<ButtonType> button = SceneManager.getInstance().showDialog(Alert.AlertType.CONFIRMATION, rb.getString("languageChange"), rb.getString("languageChange"), rb.getString("restart"));
            if (button.isPresent() && button.get() == ButtonType.OK) {
                Platform.runLater(SceneManager::restart);
            }
        });
    }

    public static void restart() {
        stage.close();
        Platform.runLater(() -> {
            try {
                new App().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
