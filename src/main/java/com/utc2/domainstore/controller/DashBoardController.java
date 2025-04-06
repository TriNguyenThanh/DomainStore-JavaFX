package com.utc2.domainstore.controller;

import com.utc2.domainstore.view.ConfigManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashBoardController implements Initializable {
    private ResourceBundle bundle;
    private Parent parent;
    @FXML
    private Button btAccount, btSearch, btBill, btPayment;

    @FXML
    private AnchorPane contentArea;

    @FXML
    public void handleButtonOnAction(ActionEvent e) {
        if (e.getSource() == btAccount) {
            changeView("/fxml/account.fxml");
        } else if (e.getSource() == btSearch) {
            changeView("/fxml/search.fxml");
        } else if (e.getSource() == btBill) {
            changeView("/fxml/transaction.fxml");
        } else if (e.getSource() == btPayment) {
            changeView("/fxml/payment.fxml");
        }
    }

    private void changeView(String fxmlPath) {
        try {
            ResourceBundle rb = ConfigManager.getInstance().getLanguageBundle();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath), rb);
            Node node = fxmlLoader.load();

            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

            contentArea.getChildren().setAll(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
    }
}
