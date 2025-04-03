package com.utc2.domainstore.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    private ResourceBundle bundle;

    @FXML
    private TextField tfSearch;

    @FXML
    private Button btSearch, btAdd;

    @FXML
    private Label lbDomain, lbStatus, lbPrice;

    @FXML
    private void handleSearch() {
        String domainName = tfSearch.getText();
        // Call the search method from DomainServices
        // DomainServices domainServices = new DomainServices();
        // JSONObject result = domainServices.search(domainName);
        // Update the UI with the result
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
    }
}
