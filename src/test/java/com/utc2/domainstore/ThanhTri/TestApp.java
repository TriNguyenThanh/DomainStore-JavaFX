package com.utc2.domainstore.ThanhTri;

import com.utc2.domainstore.view.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class TestApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        SceneManager.init(stage);
        SceneManager.getInstance().switchScene("/fxml/start.fxml");
        SceneManager.getInstance().setTitle("UTC2 - Domain Store");
        SceneManager.getInstance().show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}