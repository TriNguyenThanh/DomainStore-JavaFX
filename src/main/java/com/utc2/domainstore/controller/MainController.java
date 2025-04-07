package com.utc2.domainstore.controller;

import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private ResourceBundle bundle;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btDashBoard, btAccount, btShoppingCart, btSearch, btBill, btPayment, btUser, btDomain, btCheckBill, btCheckPayment;

    private Button focus;

    @FXML
    private void handleButtonOnAction(ActionEvent e) {
        if (e.getSource() == btDashBoard) {
            load("/fxml/dashboard.fxml");
        } else if (focus != e.getSource() && e.getSource() == btAccount) {
            load("/fxml/account.fxml");
        } else if (focus != e.getSource() && e.getSource() == btShoppingCart) {
            load("/fxml/shoppingCart.fxml");
        } else if (focus != e.getSource() && e.getSource() == btSearch) {
            load("/fxml/search.fxml");
        } else if (focus != e.getSource() && e.getSource() == btBill) {
            load("/fxml/transaction.fxml");
        } else if (focus != e.getSource() && e.getSource() == btPayment) {
            load("/fxml/payment.fxml");
        } else if (focus != e.getSource() && e.getSource() == btUser) {
            load("/fxml/user_manager.fxml");
        } else if (focus != e.getSource() && e.getSource() == btDomain) {
            load("/fxml/domain_manager.fxml");
        } else if (focus != e.getSource() && e.getSource() == btCheckBill) {
            load("/fxml/transaction_manager.fxml");
        } else if (focus != e.getSource() && e.getSource() == btCheckPayment) {
            load("/fxml/payment_manager.fxml");
        }

        focus = (Button) e.getSource();
    }

    private void load(String fxmlPath) {
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
        focus = btDashBoard;
        load("/fxml/dashboard.fxml");

        this.bundle = resources;
        SceneManager.getInstance().setResizable(true);
        SceneManager.getInstance().setMaximized(true);
    }
}
