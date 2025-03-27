package com.utc2.domainstore.view;

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
        if(stage == null) {
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

            Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setResizable(boolean resizable) {
        if (stage == null) {
            throw new IllegalStateException("Stage has not been initialized");
        }
        stage.setResizable(resizable);
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
}
