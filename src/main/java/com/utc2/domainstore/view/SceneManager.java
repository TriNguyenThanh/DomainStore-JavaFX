package com.utc2.domainstore.view;

import javafx.stage.Stage;

public class SceneManager {
    private static SceneManager instance;
    private final Stage stage;

    private SceneManager() {
        this.stage = new Stage();
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }
}
