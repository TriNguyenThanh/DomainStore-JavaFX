package com.utc2.domainstore.view;

import com.utc2.domainstore.App;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import com.utc2.domainstore.utils.MoneyCellFactory;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
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
                System.exit(0);
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

            setFadeInTransition(scene.getRoot(), Duration.millis(500d));

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

    public void setSizeToFit() {
        if (stage == null) {
            throw new IllegalStateException("Stage has not been initialized");
        }
        stage.sizeToScene();
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
        }
    }

    public void setMaximized(boolean maximized) {
        Platform.runLater(() -> {
            stage.setMaximized(maximized);
        });
    }

    public void setCenterOnScene() {
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
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(s)), width, height, false, true);
    }

    public void initLanguageComboBox(ComboBox<String> comboBox) {
        ResourceBundle rb = ConfigManager.getInstance().getLanguageBundle();

        // set value to comboBox
        comboBox.getItems().addAll(ConfigManager.getInstance().getLanguages());

        // set default value to comboBox
        comboBox.setValue(ConfigManager.getInstance().getSetting("language", ""));

        // set action to comboBox
        comboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            String selectedLanguage = comboBox.getSelectionModel().getSelectedItem();
            if (selectedLanguage == null) {
                return;
            } else {
                System.out.println("Selected language.json: " + selectedLanguage);
            }
            ConfigManager.getInstance().updateLanguage(selectedLanguage);

            // update formatters
            LocalDateCellFactory.setFormatter(ConfigManager.getInstance().getDateTimeFormatter());
            MoneyCellFactory.setFormatter(ConfigManager.getInstance().getNumberFormatter());

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

    public void setFadeInTransition(Parent root, Duration duration) {
        FadeTransition fadeIn = new FadeTransition(duration, root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public void setFadeInTransition(Parent root, Duration duration, Double fromValue, Double toValue) {
        FadeTransition fadeIn = new FadeTransition(duration, root);
        fadeIn.setFromValue(fromValue);
        fadeIn.setToValue(toValue);
        fadeIn.play();
    }

    public void setFadeOutTransition(Parent root, Duration duration) {
        FadeTransition fadeOut = new FadeTransition(duration, root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.play();
    }
}