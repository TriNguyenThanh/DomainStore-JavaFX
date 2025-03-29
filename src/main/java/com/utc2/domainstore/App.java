package com.utc2.domainstore;

import com.utc2.domainstore.view.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        ResourceBundle rb = ConfigManager.getInstance().getLanguageBundle();
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"), rb);
//
//        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
//        stage.setScene(scene);

        SceneManager.init(stage);
        SceneManager.getInstance().switchScene("/fxml/start.fxml");
        SceneManager.getInstance().setTitle("UTC2 - Domain Store");
        SceneManager.getInstance().show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}