package com.utc2.domainstore;

import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        try {
            UserSession.getInstance().setUserId(1);
            UserSession.getInstance().setRole(RoleEnum.ADMIN);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SceneManager.init(stage);
        SceneManager.getInstance().switchScene("/fxml/start.fxml");
        SceneManager.getInstance().setTitle("UTC2 - Domain Store");
        SceneManager.getInstance().setIcon("/image/logoUTC2.png");
        SceneManager.getInstance().show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}