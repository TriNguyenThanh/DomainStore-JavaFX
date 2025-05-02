package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.service.LoginServices;
import com.utc2.domainstore.utils.CheckingUtils;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private ResourceBundle bundle;

    // FXML components
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox passwordCheckbox;
    @FXML
    private Label useErrorLabel;
    @FXML
    private Label passErrorLabel;
    @FXML
    private ComboBox<String> cbLanguage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;

        SceneManager.getInstance().initLanguageComboBox(cbLanguage);

        SceneManager.getInstance().setResizable(false);

        passwordField.setOnAction(event -> {
            login();
        });
    }

    // handle login
    public void login() {
        passwordFieldOnInputMethodTextChanged();
        boolean flag = true;

        // bắt buộc nhập
        if (usernameField.getText().isBlank()) {
            useErrorLabel.setText(bundle.getString("error.username"));
            flag = false;
        } else {
            useErrorLabel.setText(" ");
        }

        String pass = passwordField.getText();
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
            LoginServices loginServices = new LoginServices();
            JSONObject respond = loginServices.authentication(request);
            try {
                int userId = respond.getInt("user_id");
                RoleEnum role = RoleEnum.valueOf(respond.get("role").toString());

                // đưa id và role vào session
                UserSession.getInstance().setUserId(userId);
                UserSession.getInstance().setRole(role);

                SceneManager.getInstance().switchScene("/fxml/main.fxml");
            } catch (Exception e) {
                useErrorLabel.setText(bundle.getString("error.login"));
                passErrorLabel.setText(bundle.getString("error.login"));
            }
        }
    }

    // handle register
    public void register() {
        SceneManager.getInstance().switchScene("/fxml/register.fxml");
    }

    public void showPassword() {
        if (passwordCheckbox.isSelected()) {
            passwordField.setPromptText(passwordField.getText());
            passwordField.setText("");
        } else {
            passwordField.setText(passwordField.getPromptText());
            passwordField.setPromptText("");
        }
    }

    public void passwordFieldOnInputMethodTextChanged() {
        if (passwordCheckbox.isSelected()) {
            passwordField.setText(passwordField.getPromptText());
            passwordField.setPromptText("");
            passwordField.positionCaret(passwordField.getText().length());
            passwordCheckbox.setSelected(false);
        }
    }
}
