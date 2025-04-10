package com.utc2.domainstore.controller;

import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.utils.CheckingUtils;
import com.utc2.domainstore.view.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

import static com.utc2.domainstore.utils.PasswordUtils.checkPassword;

public class changePasswordController implements Initializable {
    private ResourceBundle bundle;
    private String hash_pass;

    @FXML
    private Label lbOldErr, lbNewErr, lbConfirmErr;

    @FXML
    private PasswordField tfOld, tfNew, tfConfirm;

    @FXML
    private CheckBox cbPass;

    @FXML
    private Button btCancel, btSave;

    @FXML
    private void handleButtonOnAction(ActionEvent e) {
        if (e.getSource() == btCancel) {
            ((Stage) btCancel.getScene().getWindow()).close();
        } else if (e.getSource() == btSave) {
            tfOnclick();
            if (!checkOldPassword()) return;
            boolean flag = true;

            if (tfNew.getText().isBlank()) {
                lbNewErr.setText(bundle.getString("error.newPasswordErr1"));
                flag = false;
            } else if (tfNew.getText().equals(tfOld.getText())) {
                lbNewErr.setText(bundle.getString("error.newPasswordErr3"));
                flag = false;
            } else if (!CheckingUtils.passwordCheck(tfNew.getText())) {
                lbNewErr.setText(bundle.getString("error.newPasswordErr2"));
                flag = false;
            } else {
                lbNewErr.setText("");
            }

            if (!tfNew.getText().equals(tfConfirm.getText())) {
                lbConfirmErr.setText(bundle.getString("error.confirmPasswordErr"));
                flag = false;
            } else {
                lbConfirmErr.setText("");
            }

            if (!flag) return;

            // luu vao database
            JSONObject request = new JSONObject();
            request.put("user_id", UserSession.getInstance().getUserId());
            request.put("password", tfNew.getText());

            AccountServices accountServices = new AccountServices();
            JSONObject respond = accountServices.updateUserPassword(request);

            if (respond.get("status").equals("failed")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(bundle.getString("error"));
                alert.setHeaderText(bundle.getString("notice.updatePassFailed"));
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("notice"));
                alert.setHeaderText(bundle.getString("notice.updatePassSuccess"));
                alert.showAndWait();
            }
            ((Stage) tfOld.getScene().getWindow()).close();
        }
    }

    @FXML
    private void cbOnAction() {
        if (!checkOldPassword()) return;

        if (cbPass.isSelected()) {
            tfNew.setPromptText(tfNew.getText());
            tfNew.setText("");
            tfConfirm.setPromptText(tfConfirm.getText());
            tfConfirm.setText("");
        } else {
            tfNew.setText(tfNew.getPromptText());
            tfNew.setPromptText("");
            tfNew.positionCaret(tfNew.getText().length());

            tfConfirm.setText(tfConfirm.getPromptText());
            tfConfirm.setPromptText("");
            tfConfirm.positionCaret(tfConfirm.getText().length());
        }
    }

    @FXML
    private void tfOnclick() {
        if (!checkOldPassword()) return;

        if (cbPass.isSelected()) {
            tfNew.setText(tfNew.getPromptText());
            tfNew.setPromptText("");
            tfNew.positionCaret(tfNew.getText().length());

            tfConfirm.setText(tfConfirm.getPromptText());
            tfConfirm.setPromptText("");
            tfConfirm.positionCaret(tfConfirm.getText().length());

            cbPass.setSelected(false);
        }
    }

    private boolean checkOldPassword() {
        if (tfOld.getText().isBlank()) {
            lbOldErr.setText(bundle.getString("error.oldPasswordErr1"));
            return false;
        } else if (!checkPassword(hash_pass, tfOld.getText())) {
            lbOldErr.setText(bundle.getString("error.oldPasswordErr2"));
            return false;
        }
        lbOldErr.setText("");
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
    }

    public void setData(String hash_pass) {
        this.hash_pass = hash_pass;
    }
}
