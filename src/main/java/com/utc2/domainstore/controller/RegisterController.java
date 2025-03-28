package com.utc2.domainstore.controller;

import com.utc2.domainstore.service.RegisterServices;
import com.utc2.domainstore.view.SceneManager;
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

public class RegisterController implements Initializable {
    private ResourceBundle bundle;

    @FXML
    private TextField usernameTextfield;
    @FXML
    private TextField phoneTextfield;
    @FXML
    private TextField emailTextfield;
    @FXML
    private TextField psIDTextfield;
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
    private Label psIDErr;
    @FXML
    private Label passwordErr;
    @FXML
    private Label confirmPasswordErr;

    @FXML
    private CheckBox passwordCheckBox;

    public void buttonRegisterOnAction() {
        // kiểm tra và bắt lỗi
        // kiểm tra họ và tên
        boolean flag = true;

        if (usernameTextfield.getText().isBlank()) {
            flag = false;
            usernameErr.setText(bundle.getString("register.usernameErr"));
        } else {
            usernameErr.setText(" ");
        }

        // kiểm tra số điện thoại
        if (phoneTextfield.getText().isBlank()) {
            flag = false;
            phoneErr.setText(bundle.getString("register.phoneErr1"));
        } else if (!phoneNumberCheck(phoneTextfield.getText())) {
            flag = false;
            phoneErr.setText(bundle.getString("register.phoneErr2"));
        } else {
            phoneErr.setText(" ");
        }

        // kiêm tra email
        if (emailTextfield.getText().isBlank()) {
            flag = false;
            emailErr.setText(bundle.getString("register.emailErr1"));
        } else if (!emailCheck(emailTextfield.getText())) {
            flag = false;
            emailErr.setText(bundle.getString("register.emailErr2"));
        } else {
            emailErr.setText(" ");
        }

        // kiểm tra số CCCD
        if (psIDTextfield.getText().isBlank()) {
            flag = false;
            psIDErr.setText(bundle.getString("register.psIDErr1"));
        } else if (!personalIDCheck(psIDTextfield.getText())) {
            flag = false;
            psIDErr.setText(bundle.getString("register.psIDErr2"));
        } else {
            psIDErr.setText(" ");
        }

        // Kiểm tra mật khẩu
        if (passwordField.getText().isBlank()) {
            flag = false;
            passwordErr.setText(bundle.getString("register.passwordErr1"));
        } else if (!passwordCheck(passwordField.getText())) {
            flag = false;
            passwordErr.setText(bundle.getString("register.passwordErr2"));
        } else {
            passwordErr.setText(" ");
        }

        // Kiểm tra nhập lại mật khẩu
        if (confirmPasswordField.getText().isBlank()) {
            flag = false;
            confirmPasswordErr.setText(bundle.getString("register.confirmPasswordErr1"));
        } else if (!confirmPasswordField.getText().equals(passwordField.getText())) {
            flag = false;
            confirmPasswordErr.setText(bundle.getString("register.confirmPasswordErr2"));
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
            requestJSON.put("personal_id", psIDTextfield.getText());
            requestJSON.put("password", passwordField.getText());

            // nhận respone
            try {
                RegisterServices registerServices = new RegisterServices();
                JSONObject responeJSON = registerServices.addToDB(requestJSON);
                String status = responeJSON.getString("status");
                String message = responeJSON.getString("message");

                if (status.equals("success")) {
                    System.out.println("Dang ky tai khoan thanh cong");
                } else if (status.equals("failed")) {
                    if (message.toLowerCase().contains("phone number already exists")) {
                        phoneErr.setText(bundle.getString("register.phoneErr3"));
                        System.out.println("So dien thoai da ton tai. Hay su dung mot so dien thoai khac");
                    }
                    if (message.toLowerCase().contains("email already exists")) {
                        emailErr.setText(bundle.getString("register.emailErr3"));
                        System.out.println("Email da ton tai. Hay su dung mot email khac");
                    }
                    if (message.toLowerCase().contains("personal id already exists")) {
                        psIDErr.setText(bundle.getString("register.psIDErr3"));
                        System.out.println("So CCCD da ton tai. Hay su dung mot so CCCD khac");
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException("Can't connect to service");
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

    // sdt phải bắt đầu bằng 0 hoặc +84 và có độ dài là 9
    private boolean phoneNumberCheck(String s) {
        String pattern = "^(0|\\+84)\\d{9}$";
        return s.matches(pattern);
    }

    // kiểm tra số căn cước công dân
    private boolean personalIDCheck(String s) {
        String pattern = "^(0|)\\d{12}$";
        return s.matches(pattern);
    }

    // kiểm tra email
    private boolean emailCheck(String s) {
        String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return s.matches(pattern);
    }

    //kiểm tra mặt khẩu có độ dài 8-16 ký tự, bao gồm a - z, A - Z, 0 - 9, @, _, .
    private boolean passwordCheck(String s) {
        String pattern = "^[a-zA-Z0-9@_]{8,16}$";
        return s.matches(pattern);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        SceneManager.getInstance().setResizable(false);
    }
}
