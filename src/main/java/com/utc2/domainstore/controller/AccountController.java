package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.AccountModel;
import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.service.IAccount;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.utc2.domainstore.utils.CheckingUtils.*;

public class AccountController implements Initializable {
    private ResourceBundle bundle;
    private AccountModel rootData;
    private AccountModel newData;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField tfUsername, tfPhone, tfEmail, tfPsID;

    @FXML
    private Label lbFullNameErr, lbPhoneErr, lbEmailErr, lbPsIDErr;

    @FXML
    private PasswordField tfPass;

    @FXML
    private Button btEdit, btSave, btCancel, btLogout, btChangePass;

    @FXML
    private void handleButtonOnAction(ActionEvent e) {
        if (e.getSource() == btEdit) {
            edit(true);
        } else if (e.getSource() == btSave) {
            // lưu
            save();
        } else if (e.getSource() == btCancel) {
            // hủy bỏ thay đổi
            cancel();
        } else if (e.getSource() == btLogout) {
            //
            logout();
        } else if (e.getSource() == btChangePass) {
            changePass();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
        rootData = getRootData();
        newData = null;
        displayData();
        rootPane.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent == null) {
                onRemovedFromScene();
            }
        });
    }

    private AccountModel getRootData() {
        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());

        IAccount accountServices = new AccountServices();
        JSONObject respond = accountServices.getUserInformation(request);

        String fullname = respond.getString("username");
        String phone = respond.getString("phone");
        String email = respond.getString("email");
        String psID = respond.getString("personal_id");
        String pass = respond.getString("password");

        return new AccountModel(fullname, phone, email, psID, pass);
    }

    private void displayData() {
        tfUsername.setText(rootData.getFullName());
        tfPhone.setText(rootData.getPhone());
        tfEmail.setText(rootData.getEmail());
        tfPsID.setText(rootData.getPsID());
        tfPass.setText("11111111");
    }

    private void onRemovedFromScene() {
        //check the difference
        save();
    }

    private void logout() {
        edit(false);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("logout"));
        alert.setHeaderText(bundle.getString("notice.logout"));
        alert.setContentText("");
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            // Reset the fields to the original data
            save();
            UserSession.getInstance().logout();
            SceneManager.getInstance().switchScene("/fxml/login.fxml");
        }
    }

    private void changePass() {
        try {
            ResourceBundle rb = ConfigManager.getInstance().getLanguageBundle();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/change_password.fxml"), rb);
            Parent root = fxmlLoader.load();

            ChangePasswordController controller = fxmlLoader.getController();
            controller.setData(rootData.getHash_password());

            Stage stage = new Stage();
            stage.setTitle(bundle.getString("changePass"));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/icon/password.png"))));

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            rootData = newData = getRootData();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void cancel() {
        edit(false);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("cancel"));
        alert.setHeaderText(bundle.getString("notice.cancel"));
        alert.setContentText("");
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            // Reset the fields to the original data
            displayData();
            lbPhoneErr.setText("");
            lbEmailErr.setText("");
            lbPsIDErr.setText("");
            lbFullNameErr.setText("");

        }
    }

    private void save() {
        edit(false);
        newData = new AccountModel(tfUsername.getText(), tfPhone.getText(), tfEmail.getText(), tfPsID.getText(), rootData.getHash_password());
        if (rootData.isSame(newData)) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("save"));
        alert.setHeaderText(bundle.getString("notice.save"));
        alert.setContentText("");

        Optional<ButtonType> button = alert.showAndWait();
        if (button.isPresent() && button.get() == ButtonType.OK) {

            if (!checkingData()) return;

            JSONObject request = new JSONObject();
            request.put("user_id", UserSession.getInstance().getUserId());
            request.put("username", newData.getFullName());
            request.put("phone", newData.getPhone());
            request.put("email", newData.getEmail());
            request.put("personal_id", newData.getPsID());

            IAccount accountServices = new AccountServices();
            JSONObject respond = accountServices.updateUser(request);
            try {
                String status, message;
                status = respond.getString("status");
                message = respond.getString("message");

                if (status.equals("failed")) {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle(bundle.getString("save"));
                    alert1.setHeaderText(bundle.getString("save"));
                    alert1.setContentText(bundle.getString("notice.savingFailed"));
                    alert1.showAndWait();
                }
            } catch (JSONException e) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle(bundle.getString("save"));
                alert1.setHeaderText(bundle.getString("save"));
                alert1.setContentText(bundle.getString("notice.savingFailed"));
                alert1.showAndWait();
            }
        }
    }

    private boolean checkingData() {
        boolean flag = true;

        // kiểm tra tên người dùng
        if (newData.getFullName().isBlank()) {
            flag = false;
            lbFullNameErr.setText(bundle.getString("register.usernameErr"));
        } else {
            lbFullNameErr.setText(" ");
        }

        // kiểm tra số điện thoại
        if (newData.getPhone().isBlank()) {
            flag = false;
            lbPhoneErr.setText(bundle.getString("register.phoneErr1"));
        } else if (!phoneNumberCheck(newData.getPhone())) {
            flag = false;
            lbPhoneErr.setText(bundle.getString("register.phoneErr2"));
        } else {
            lbPhoneErr.setText(" ");
        }

        // kiêm tra email
        if (newData.getEmail().isBlank()) {
            flag = false;
            lbEmailErr.setText(bundle.getString("register.emailErr1"));
        } else if (!emailCheck(newData.getEmail())) {
            flag = false;
            lbEmailErr.setText(bundle.getString("register.emailErr2"));
        } else {
            lbEmailErr.setText(" ");
        }

        // kiểm tra số CCCD
        if (newData.getPsID().isBlank()) {
            flag = false;
            lbPsIDErr.setText(bundle.getString("register.psIDErr1"));
        } else if (!personalIDCheck(newData.getPsID())) {
            flag = false;
            lbPsIDErr.setText(bundle.getString("register.psIDErr2"));
        } else {
            lbPsIDErr.setText(" ");
        }

        return flag;
    }

    private void edit(boolean isEdited) {
        tfUsername.setEditable(isEdited);
        tfPhone.setEditable(isEdited);
        tfEmail.setEditable(isEdited);
        tfPsID.setEditable(isEdited);
        if (isEdited) {
            tfUsername.setStyle("-fx-border-color: #0000C6");
            tfPhone.setStyle("-fx-border-color: #0000C6");
            tfEmail.setStyle("-fx-border-color: #0000C6");
            tfPsID.setStyle("-fx-border-color: #0000C6");
        } else {
            tfUsername.setStyle("-fx-border-color: #FFFFFF");
            tfPhone.setStyle("-fx-border-color: #FFFFFF");
            tfEmail.setStyle("-fx-border-color: #FFFFFF");
            tfPsID.setStyle("-fx-border-color: #FFFFFF");
        }
    }
}
