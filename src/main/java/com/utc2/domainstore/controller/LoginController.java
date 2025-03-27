package com.utc2.domainstore.controller;

import com.utc2.domainstore.view.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox passwordCheckbox;
    @FXML private Label useErrorLabel;
    @FXML private Label passErrorLabel;

    private ResourceBundle bundle;

    public void login() {
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

        if (!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {
            if (usernameField.getText().equals("admin") && passwordField.getText().equals("admin")) {
//                SceneManager.getInstance().switchScene("/fxml/dashboard.fxml");
                System.out.println("Login successful");
            } else {
                useErrorLabel.setText(bundle.getString("login.loginErr"));
                passErrorLabel.setText(bundle.getString("login.loginErr"));
            }
        }
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        SceneManager.getInstance().setResizable(false);
    }
}
