package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.service.LoginServices;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    @FXML
    private ComboBox<String> cbLanguage;

    private ResourceBundle bundle;

    public void login() {
        passwordFieldOnInputMethodTextChanged();
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

    @FXML
    private void changeLanguage() {
        String selectedLanguage = cbLanguage.getValue();
        ConfigManager.getInstance().updateSetting("language", selectedLanguage);
//        SceneManager.getInstance().setLanguage(selectedLanguage);
//        bundle = ConfigManager.getInstance().getLanguageBundle();
//        usernameField.setPromptText(bundle.getString("login.username"));
//        passwordField.setPromptText(bundle.getString("login.password"));
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle(bundle.getString("login.languageChange"));
//        alert.setHeaderText(null);
//        alert.setContentText(bundle.getString("login.languageChangeContent"));
//        Op alert.showAndWait();

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
        this.cbLanguage.getItems().addAll(ConfigManager.getInstance().getLanguages());
        this.cbLanguage.setValue(ConfigManager.getInstance().getSetting("language", "Tiếng việt"));
        SceneManager.getInstance().setResizable(false);
        SceneManager.getInstance().setMaximized(false);
    }
}
