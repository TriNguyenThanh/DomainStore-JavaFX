package com.utc2.domainstore.view;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {
    private static SceneManager instance;
    private Stage stage;

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void init(Stage stage) {
        if(this.stage != null) {
            this.stage = stage;
        }
    }

    public void switchScene(String fxmlPath) {
        try {
            stage.getScene().setRoot(FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
