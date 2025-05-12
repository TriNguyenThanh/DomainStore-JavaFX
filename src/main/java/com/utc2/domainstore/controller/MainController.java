package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private ResourceBundle bundle;
    private static MainController instance;
    private Timeline autoRefreshTimeline;

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
    private String currentFxmlPath = "/fxml/dashboard.fxml";

    @FXML
    private void handleButtonOnAction(ActionEvent e) {
        if (e.getSource() == btDashBoard) {
            setCurrentFxmlPath("/fxml/dashboard.fxml");
        } else if (e.getSource() == btAccount) {
            setCurrentFxmlPath("/fxml/account.fxml");
        } else if (e.getSource() == btShoppingCart) {
            setCurrentFxmlPath("/fxml/shoppingCart.fxml");
        } else if (e.getSource() == btMyDomain) {
            setCurrentFxmlPath("/fxml/myDomain.fxml");
        } else if (e.getSource() == btSearch) {
            setCurrentFxmlPath("/fxml/search.fxml");
        } else if (e.getSource() == btBill) {
            setCurrentFxmlPath("/fxml/transaction.fxml");
        } else if (e.getSource() == btPayment) {
            setCurrentFxmlPath("/fxml/payment.fxml");
        } else if (e.getSource() == btUser) {
            setCurrentFxmlPath("/fxml/user_manager.fxml");
        } else if (e.getSource() == btDomain) {
            setCurrentFxmlPath("/fxml/domain_manager.fxml");
        } else if (e.getSource() == btCheckBill) {
            setCurrentFxmlPath("/fxml/confirmTransaction.fxml");
        } else if (e.getSource() == btTLD) {
            setCurrentFxmlPath("/fxml/tld_manager.fxml");
        }
        load(currentFxmlPath, false);
        focus = (Button) e.getSource();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        focus = btDashBoard;
        setCurrentFxmlPath("/fxml/dashboard.fxml");
        load("/fxml/dashboard.fxml", false);

        roleControl();
        SceneManager.getInstance().setMaximized(true);
    }

    private void roleControl() {
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

    public FXMLLoader load(String fxmlPath, boolean isRefresh) {
        FXMLLoader fxmlLoader = null;
        try {
            ResourceBundle rb = ConfigManager.getInstance().getLanguageBundle();
            URL url = MainController.class.getResource(fxmlPath);
            fxmlLoader = new FXMLLoader(url, rb);
            Node node = fxmlLoader.load();

            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

            if (!isRefresh) {
                setFadeTransition(node);
            } else {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(node);
            }
            setRefesh();
        } catch (IOException e) {
            e.printStackTrace();
        }
        focus = null;
        System.gc();
        return fxmlLoader;
    }

    private void setFadeTransition(Node node) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), contentArea);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.5);
        fadeOut.setOnFinished(event -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(100), contentArea);
            fadeIn.setFromValue(0.5);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    public void setFocus(Button button) {
        this.focus = button;
    }

    public void setAutoRefresh(Boolean isAutoRefresh) {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop(); // dừng nếu đã có
        }
        if (!isAutoRefresh) {
            return;
        }
        autoRefreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
                    Platform.runLater(() -> {
                        System.out.println("Auto refresh " + currentFxmlPath);
                        load(currentFxmlPath, true);
                    });
                })
        );
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE); // lặp mãi
        autoRefreshTimeline.play();
    }

    public void setRefesh() {
        // refresh the content area when press F5
        contentArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) {
                System.out.println("Refresh " + currentFxmlPath);
                load(currentFxmlPath, true);
            }
        });
    }

    public void setCurrentFxmlPath(String currentFxmlPath) {
        if (currentFxmlPath.equals("/fxml/dashboard.fxml") ||
                currentFxmlPath.equals("/fxml/search.fxml") ||
                currentFxmlPath.equals("/fxml/account.fxml")) {
            setAutoRefresh(false);
        } else {
//            setAutoRefresh(true);
        }
        this.currentFxmlPath = currentFxmlPath;
    }
}
