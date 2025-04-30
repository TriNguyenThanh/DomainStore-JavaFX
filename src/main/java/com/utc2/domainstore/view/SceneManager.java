package com.utc2.domainstore.view;

//import animatefx.animation.Shake;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
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

            boolean resizable = stage.isResizable();
            stage.setResizable(true);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
            if (!resizable) {
                stage.setResizable(false);
            }

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
}
