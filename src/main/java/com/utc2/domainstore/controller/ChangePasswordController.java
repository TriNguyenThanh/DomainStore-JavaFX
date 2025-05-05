package com.utc2.domainstore.controller;

import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.utils.CheckingUtils;
import com.utc2.domainstore.view.SceneManager;
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

public class ChangePasswordController implements Initializable {
    private ResourceBundle bundle;
    private String hash_pass;
    private AccountServices accountServices;

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
            if (checkOldPassword()) return;
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

            JSONObject respond = accountServices.updateUserPassword(request);

            if (respond.get("status").equals("failed")) {
                SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, bundle.getString("error"), null, bundle.getString("notice.updatePassFailed"));
            } else {
                SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("notice"), null, bundle.getString("notice.updatePassSuccess"));
            }
            ((Stage) tfOld.getScene().getWindow()).close();
        }
    }

    @FXML
    private void cbOnAction() {
        if (checkOldPassword()) return;

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
        if (checkOldPassword()) return;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        accountServices = new AccountServices();
    }

    private boolean checkOldPassword() {
        if (tfOld.getText().isBlank()) {
            lbOldErr.setText(bundle.getString("error.oldPasswordErr1"));
            return true;
        } else if (!checkPassword(hash_pass, tfOld.getText())) {
            lbOldErr.setText(bundle.getString("error.oldPasswordErr2"));
            return true;
        }
        lbOldErr.setText("");
        return false;
    }


    public void setData(String hash_pass) {
        this.hash_pass = hash_pass;
    }
}
