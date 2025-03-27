package com.utc2.domainstore.controller;

import com.utc2.domainstore.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox passwordCheckbox;

    public void login() {
        System.out.println("Login button clicked");
    }

    public void register() {
        System.out.println("Register button clicked");
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
}
