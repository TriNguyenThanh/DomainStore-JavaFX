package com.utc2.domainstore.controller;

import com.utc2.domainstore.config.JDBC;
import com.utc2.domainstore.view.SceneManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StartController implements Initializable {
    private ResourceBundle bundle;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private VBox container;

    private List<String> taskList = new ArrayList<>();

    Task<Void> task = new Task<>() {
        @Override
        protected Void call() throws Exception {
            boolean success = true;
            for (int i = 0; i < taskList.size(); i++) {
                // i là một biến bất đồng bộ nên không thể truy cập trong thread khác (main thread)
                int index = i; // Sao chép giá trị của i cho index
                boolean status = false;
                switch (i + 1) {
                    // task 1 kiểm tra kết nối database
                    case 1:
                        Connection connection = null;
                        try {
                            connection = JDBC.getConnection();
                        } catch (Exception e) {
                            status = false;
                        } finally {
                            JDBC.closeConnection(connection);
                        }
                        if (connection != null) {
                            status = true;
                        }
                        break;
                    // Kiểm tra kết nối internet
                    case 2:
                        String host = "8.8.8.8"; // Google DNS
                        int timeout = 10000; // 10 giây
                        try {
                            status = InetAddress.getByName(host).isReachable(timeout);
                        } catch (IOException e) {
                            status = false;
                        }
                        break;
                }

                Label newTask = new Label(taskList.get(index));
                if (!status) {
                    success = false;
                    newTask.setStyle("-fx-text-fill: #FF0000;");
                    updateProgress(-1, taskList.size());

                    String title = bundle.getString("task");
                    String header = bundle.getString("task");
                    String content = taskList.get(index) + " " + bundle.getString("failed");

                    Platform.runLater(() -> {
                        container.getChildren().add(newTask);
                        // show alert and exit the application
                        SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, title, header, content);
                        Platform.exit();
                    });
                    break;
                }
                updateProgress(i + 1, taskList.size());
            }
            if (success) {
                Platform.runLater(() -> {
                    SceneManager.getInstance().switchScene("/fxml/login.fxml");
                });
            }
            return null;
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
//        SceneManager.getInstance().setResizable(false);

        int i = 1;
        while (bundle.containsKey("task" + i)) {
            taskList.add(bundle.getString("task" + i));
            i++;
        }
        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
}
