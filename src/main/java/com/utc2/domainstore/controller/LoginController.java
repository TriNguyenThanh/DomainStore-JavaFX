package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.service.LoginServices;
import com.utc2.domainstore.utils.CheckingUtils;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private ResourceBundle bundle;
    private LoginServices loginServices;

    // FXML components
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label useErrorLabel, passErrorLabel, lbRecovery;
    @FXML
    private ComboBox<String> cbLanguage;
    @FXML
    private Button btLogin, btRegister;
    @FXML
    private CheckBox cbShowPassword;

    @FXML
    private void handleButton(ActionEvent event) {
        if (event.getSource() == btLogin) {
            login();
        } else if (event.getSource() == btRegister) {
            register();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.bundle = resources;
        this.loginServices = new LoginServices();

        SceneManager.getInstance().initLanguageComboBox(cbLanguage);
        SceneManager.getInstance().setResizable(false);

        passwordField.setOnAction(event -> {
            login();
        });

        passwordField.setOnMouseClicked(event -> {
            if (cbShowPassword.isSelected()) {
                hidePassword();
            }
        });

        cbShowPassword.setOnAction(actionEvent -> {
            if (cbShowPassword.isSelected()) {
                showPassword();
            } else {
                hidePassword();
            }
        });

        lbRecovery.setOnMouseClicked(mouseEvent -> {
            showChangePassword();
        });
    }
    
    // handle login
    public void login() {
        if (cbShowPassword.isSelected()) {
            hidePassword();
        }
        boolean flag = true;

        // bắt buộc nhập
        if (usernameField.getText().isBlank()) {
            useErrorLabel.setText(bundle.getString("error.username"));
            flag = false;
        } else {
            useErrorLabel.setText(" ");
        }

        if (passwordField.getText().isBlank()) {
            passErrorLabel.setText(bundle.getString("error.password1"));
            flag = false;
        } else if (!CheckingUtils.passwordCheck(passwordField.getText())) {
            passErrorLabel.setText(bundle.getString("error.password2"));
            flag = false;
        } else {
            passErrorLabel.setText(" ");
        }

        // Kiểm tra tên đăng nhập và mật khẩu
        if (flag) {
            // tạo request
            JSONObject request = new JSONObject();
            request.put("username", usernameField.getText());
            request.put("password", passwordField.getText());

            // gửi request và nhận respond

            JSONObject respond = null;
            try {
                respond = loginServices.authentication(request);
                int userId = respond.getInt("user_id");
                RoleEnum role = RoleEnum.valueOf(respond.get("role").toString().toUpperCase());

                // đưa id và role vào session
                UserSession.getInstance().setUserId(userId);
                UserSession.getInstance().setRole(role);

                SceneManager.getInstance().switchScene("/fxml/main.fxml");
            } catch (Exception e) {
                if (e.getMessage().contains("No user") || e.getMessage().contains("not found")) {
                    useErrorLabel.setText(bundle.getString("error.login"));
                    passErrorLabel.setText(bundle.getString("error.login"));
                } else if (respond.getString("error").contains("locked")) {
                    SceneManager.getInstance().showDialog(Alert.AlertType.WARNING, bundle.getString("error"), null, bundle.getString("notice.userIsBlocked"));
                }
            }
        }
    }

    // handle register
    public void register() {
        SceneManager.getInstance().switchScene("/fxml/register.fxml");
    }

    private void showChangePassword() {
        Stage secondStage = new Stage();
        secondStage.setTitle("Change password");
        secondStage.getIcons().setAll(SceneManager.getInstance().getIcon("/icon/password.png", 32, 32));

        secondStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.gc();
            }
        });
        ResourceBundle rb = ConfigManager.getInstance().getLanguageBundle();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/change_password.fxml"), rb);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        secondStage.setScene(scene);
        secondStage.setResizable(false);
        secondStage.centerOnScreen();

        secondStage.showAndWait();
    }

    public void showPassword() {
        cbShowPassword.setSelected(true);
        passwordField.setPromptText(passwordField.getText());
        passwordField.setText("");
    }

    public void hidePassword() {
        cbShowPassword.setSelected(false);
        passwordField.setText(passwordField.getPromptText());
        passwordField.setPromptText("");
        passwordField.positionCaret(passwordField.getText().length());
    }
}
