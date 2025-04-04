package com.utc2.domainstore.controller;

import com.utc2.domainstore.service.LoginServices;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
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

    private ResourceBundle bundle;

    public void login() {

        // bắt buộc nhập
        if (usernameField.getText().isBlank()) {
            useErrorLabel.setText(bundle.getString("login.usernameErr"));
        } else {
            useErrorLabel.setText(" ");
        }

        if (passwordField.getText().isBlank()) {
            passErrorLabel.setText(bundle.getString("login.passwordErr"));
        } else {
            passErrorLabel.setText(" ");
        }

        // Kiểm tra tên đăng nhập và mật khẩu
        if (!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {
            // tạo request
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", usernameField.getText());
            jsonObject.put("password", passwordField.getText());

            // gửi request và nhận respone
            LoginServices loginServices = new LoginServices();
            JSONObject response = loginServices.authentication(jsonObject);

            try {
                JSONObject jsonResponse = new JSONObject(response);
                int userId = jsonResponse.getInt("user_id");
                String role = jsonResponse.getString("role");

                // đưa id và role vào session
                UserSession.getInstance().setUserId(userId);
                UserSession.getInstance().setRole(role);

                SceneManager.getInstance().switchScene("/fxml/main.fxml");
            } catch (JSONException e) {
                useErrorLabel.setText(bundle.getString("login.loginErr"));
                passErrorLabel.setText(bundle.getString("login.loginErr"));
            }
        }
    }

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        SceneManager.getInstance().setResizable(false);
    }
}
