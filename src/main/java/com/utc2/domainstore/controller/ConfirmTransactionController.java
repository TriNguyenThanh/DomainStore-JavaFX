package com.utc2.domainstore.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmTransactionController implements Initializable {
    private ResourceBundle bundle;

    @FXML
    private void onHandleButton() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
    }
}
