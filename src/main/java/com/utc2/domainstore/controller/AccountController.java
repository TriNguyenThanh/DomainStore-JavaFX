package com.utc2.domainstore.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable {
    private ResourceBundle bundle;

    @FXML
    private TextField tfUsername, tfPhone, tfEmail, tfPsID;

    @FXML
    private PasswordField tfPass;

    @FXML
    private Button btEdit, btSave, btCancel, btLogout;

    @FXML
    private void handleButtonOnAction(ActionEvent e) {
        if (e.getSource() == btEdit) {
            tfUsername.setEditable(true);
            tfPhone.setEditable(true);
            tfEmail.setEditable(true);
            tfPsID.setEditable(true);
            tfPass.setEditable(true);
        } else if (e.getSource() == btSave) {
            // lưu
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(bundle.getString("button.save"));
            alert.setHeaderText(bundle.getString("account.save"));
            alert.setContentText("");
            alert.showAndWait();
            tfUsername.setEditable(false);
            tfPhone.setEditable(false);
            tfEmail.setEditable(false);
            tfPsID.setEditable(false);
            tfPass.setEditable(false);
        } else if (e.getSource() == btCancel) {
            // hủy bỏ thay đổi
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(bundle.getString("button.cancel"));
            alert.setHeaderText(bundle.getString("account.cancel"));
            alert.setContentText("");
            alert.showAndWait();
            tfUsername.setEditable(false);
            tfPhone.setEditable(false);
            tfEmail.setEditable(false);
            tfPsID.setEditable(false);
            tfPass.setEditable(false);
        } else if (e.getSource() == btLogout) {
            //
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(bundle.getString("button.logout"));
            alert.setHeaderText(bundle.getString("account.logout"));
            alert.setContentText("");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
        
    }
}
