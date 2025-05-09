package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private ResourceBundle bundle;
    private static MainController instance;

    public MainController() {
        // Private constructor to prevent instantiation
        instance = this;
    }

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btDashBoard, btAccount, btShoppingCart, btMyDomain, btSearch, btBill, btPayment, btUser, btDomain, btCheckBill, btTLD;

    private Button focus;

    @FXML
    private void handleButtonOnAction(ActionEvent e) {
        if (e.getSource() == btDashBoard) {
            load("/fxml/dashboard.fxml");
        } else if (focus != e.getSource() && e.getSource() == btAccount) {
            load("/fxml/account.fxml");
        } else if (focus != e.getSource() && e.getSource() == btShoppingCart) {
            load("/fxml/shoppingCart.fxml");
        } else if (focus != e.getSource() && e.getSource() == btMyDomain) {
            load("/fxml/myDomain.fxml");
        } else if (focus != e.getSource() && e.getSource() == btSearch) {
            load("/fxml/search.fxml");
        } else if (focus != e.getSource() && e.getSource() == btBill) {
            load("/fxml/transaction.fxml");
        } else if (focus != e.getSource() && e.getSource() == btPayment) {
            load("/fxml/payment.fxml");
        } else if (focus != e.getSource() && e.getSource() == btUser && UserSession.getInstance().getRole() == RoleEnum.admin) {
            load("/fxml/user_manager.fxml");
        } else if (focus != e.getSource() && e.getSource() == btDomain && UserSession.getInstance().getRole() == RoleEnum.admin) {
            load("/fxml/domain_manager.fxml");
        } else if (focus != e.getSource() && e.getSource() == btCheckBill && UserSession.getInstance().getRole() == RoleEnum.admin) {
            load("/fxml/confirmTransaction.fxml");
        } else if (focus != e.getSource() && e.getSource() == btTLD && UserSession.getInstance().getRole() == RoleEnum.admin) {
            load("/fxml/tld_manager.fxml");
        }

        focus = (Button) e.getSource();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        focus = btDashBoard;
        load("/fxml/dashboard.fxml");

        roleControll();
        SceneManager.getInstance().setMaximized(true);
    }

    private void roleControll() {
        if (UserSession.getInstance().getRole() == RoleEnum.user) {
            btUser.setDisable(true);
            btUser.setGraphic(null);
            btUser.setText("");

            btCheckBill.setDisable(true);
            btCheckBill.setGraphic(null);
            btCheckBill.setText("");

            btTLD.setDisable(true);
            btTLD.setGraphic(null);
            btTLD.setText("");

            btDomain.setDisable(true);
            btDomain.setGraphic(null);
            btDomain.setText("");
        }
    }

    public FXMLLoader load(String fxmlPath) {
        FXMLLoader fxmlLoader = null;
        try {
            ResourceBundle rb = ConfigManager.getInstance().getLanguageBundle();
            fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath), rb);
            Node node = fxmlLoader.load();

            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

            contentArea.getChildren().setAll(node);

            // refresh the content area when press F5
            contentArea.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.F5) {
                    load(fxmlPath);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        focus = null;
        return fxmlLoader;
    }

    public void setFocus(Button button) {
        this.focus = button;
    }
}