package com.utc2.domainstore.controller;

import com.utc2.domainstore.service.RegisterServices;
import com.utc2.domainstore.view.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.utc2.domainstore.utils.CheckingUtils.*;

public class RegisterController implements Initializable {
    private ResourceBundle bundle;

    @FXML
    private TextField usernameTextfield;
    @FXML
    private TextField phoneTextfield;
    @FXML
    private TextField emailTextfield;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label usernameErr;
    @FXML
    private Label phoneErr;
    @FXML
    private Label emailErr;
    @FXML
    private Label passwordErr;
    @FXML
    private Label confirmPasswordErr;

    @FXML
    private ComboBox<String> cbLanguages;

    @FXML
    private CheckBox passwordCheckBox;

    public void buttonRegisterOnAction() {
        // kiểm tra và bắt lỗi
        // kiểm tra họ và tên
        passwordFieldOnClicked();
        boolean flag = true;

        if (usernameTextfield.getText().isBlank()) {
            flag = false;
            usernameErr.setText(bundle.getString("error.username"));
        } else {
            usernameErr.setText(" ");
        }

        // kiểm tra số điện thoại
        if (phoneTextfield.getText().isBlank()) {
            flag = false;
            phoneErr.setText(bundle.getString("error.phone1"));
        } else if (!phoneNumberCheck(phoneTextfield.getText())) {
            flag = false;
            phoneErr.setText(bundle.getString("error.phone2"));
        } else {
            phoneErr.setText(" ");
        }

        // kiêm tra email
        if (emailTextfield.getText().isBlank()) {
            flag = false;
            emailErr.setText(bundle.getString("error.email1"));
        } else if (!emailCheck(emailTextfield.getText())) {
            flag = false;
            emailErr.setText(bundle.getString("error.email2"));
        } else {
            emailErr.setText(" ");
        }

        // Kiểm tra mật khẩu
        if (passwordField.getText().isBlank()) {
            flag = false;
            passwordErr.setText(bundle.getString("error.password1"));
        } else if (!passwordCheck(passwordField.getText())) {
            flag = false;
            passwordErr.setText(bundle.getString("error.password2"));
        } else {
            passwordErr.setText(" ");
        }

        // Kiểm tra nhập lại mật khẩu
        if (confirmPasswordField.getText().isBlank()) {
            flag = false;
            confirmPasswordErr.setText(bundle.getString("error.confirmPasswordErr1"));
        } else if (!confirmPasswordField.getText().equals(passwordField.getText())) {
            flag = false;
            confirmPasswordErr.setText(bundle.getString("error.confirmPasswordErr2"));
        } else {
            confirmPasswordErr.setText(" ");
        }

        // gửi request và nhận respone từ service
        if (flag) {
            // tạo request
            JSONObject requestJSON = new JSONObject();
            requestJSON.put("username", usernameTextfield.getText());
            requestJSON.put("phone", phoneTextfield.getText());
            requestJSON.put("email", emailTextfield.getText());
            requestJSON.put("password", passwordField.getText());
            requestJSON.put("role", "user");

            // nhận respone
            try {
                RegisterServices registerServices = new RegisterServices();
                JSONObject respondJSON = registerServices.addToDB(requestJSON);
                String status = respondJSON.getString("status");
                String message = respondJSON.getString("message");

                if (status.equals("success")) {
                    System.out.println("Dang ky tai khoan thanh cong");
                    SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("register"), null, bundle.getString("notice.registerSuccess"));
                    SceneManager.getInstance().switchScene("/fxml/login.fxml");
                } else if (status.equals("failed")) {
                    if (message.toLowerCase().contains("phone number already exists")) {
                        phoneErr.setText(bundle.getString("error.phone3"));
                    }
                    if (message.toLowerCase().contains("email already exists")) {
                        emailErr.setText(bundle.getString("error.email3"));
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException("Can't connect to service");
            } catch (SQLException e) {
                if (e.getMessage().toLowerCase().contains("phone number")) {
                    phoneErr.setText(bundle.getString("error.phone4"));
                }
                if (e.getMessage().toLowerCase().contains("email")) {
                    emailErr.setText(bundle.getString("error.email4"));
                }
            }
        }
    }

    public void buttonLoginOnAction() {
        SceneManager.getInstance().switchScene("/fxml/login.fxml");
    }

    public void showPasswordOnAction() {
        if (passwordCheckBox.isSelected()) {
            passwordField.setPromptText(passwordField.getText());
            passwordField.setText("");
            confirmPasswordField.setPromptText(confirmPasswordField.getText());
            confirmPasswordField.setText("");
        } else {
            passwordField.setText(passwordField.getPromptText());
            passwordField.setPromptText("");
            passwordField.positionCaret(passwordField.getText().length());

            confirmPasswordField.setText(confirmPasswordField.getPromptText());
            confirmPasswordField.setPromptText("");
            confirmPasswordField.positionCaret(confirmPasswordField.getText().length());
        }
    }

    public void passwordFieldOnClicked() {
        if (passwordCheckBox.isSelected()) {
            passwordField.setText(passwordField.getPromptText());
            passwordField.setPromptText("");
            passwordField.positionCaret(passwordField.getText().length());

            confirmPasswordField.setText(confirmPasswordField.getPromptText());
            confirmPasswordField.setPromptText("");
            confirmPasswordField.positionCaret(confirmPasswordField.getText().length());

            passwordCheckBox.setSelected(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        SceneManager.getInstance().initLanguageComboBox(cbLanguages);
        SceneManager.getInstance().setResizable(false);
    }
}
