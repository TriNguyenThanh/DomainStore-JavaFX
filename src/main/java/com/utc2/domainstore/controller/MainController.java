package com.utc2.domainstore.controller;

import com.utc2.domainstore.view.SceneManager;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        SceneManager.getInstance().setMaximized(true);
    }
}
