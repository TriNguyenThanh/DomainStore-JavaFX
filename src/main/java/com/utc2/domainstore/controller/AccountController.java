package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.AccountModel;
import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.service.IAccount;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.utc2.domainstore.utils.CheckingUtils.emailCheck;
import static com.utc2.domainstore.utils.CheckingUtils.phoneNumberCheck;

public class AccountController implements Initializable {
    private ResourceBundle bundle;
    private AccountModel rootData;
    private AccountModel newData;
    private IAccount accountServices;

    // FXML
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField tfUsername, tfPhone, tfEmail;
    @FXML
    private Label lbFullNameErr, lbPhoneErr, lbEmailErr;
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
            showChangePassword();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
        accountServices = new AccountServices();

        rootData = getRootData();
        newData = null;
        displayData();
        btCancel.setDisable(true);
        btSave.setDisable(true);
        rootPane.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent == null) {
                onRemovedFromScene();
            }
        });
    }

    // get data from server
    private AccountModel getRootData() {

        // create a request to get user information
        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());

        JSONObject respond = accountServices.getUserInformation(request);

        // parse the response
        String fullname = respond.getString("username");
        String phone = respond.getString("phone");
        String email = respond.getString("email");
        String pass = respond.getString("password");

        return new AccountModel(fullname, phone, email, pass);
    }

    // display data on the screen
    private void displayData() {
        tfUsername.setText(rootData.getFullName());
        tfPhone.setText(rootData.getPhone());
        tfEmail.setText(rootData.getEmail());
        tfPass.setText("11111111");
    }

    // check if the data is changed
    private void onRemovedFromScene() {
        //check the difference
        save();
    }

    // logout
    private void logout() {
        edit(false);
        Optional<ButtonType> buttonType = SceneManager.getInstance().showDialog(Alert.AlertType.CONFIRMATION,
                bundle.getString("logout"), bundle.getString("notice.logout"), null);
        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            // Reset the fields to the original data
            save();
            UserSession.getInstance().logout();
            SceneManager.getInstance().switchScene("/fxml/login.fxml");
        }
    }

    // change password
    private void showChangePassword() {
        Stage secondStage = new Stage();
        secondStage.setTitle("Change password");
        secondStage.getIcons().setAll(SceneManager.getInstance().getIcon("/icon/password.png", 32, 32));

        secondStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.gc();
            }
        });
        ResourceBundle rb = ConfigManager.getInstance().getLanguageBundle();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/change_password.fxml"), rb);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        secondStage.setScene(scene);
        secondStage.setResizable(false);
        secondStage.centerOnScreen();

        secondStage.showAndWait();
    }

    // cancel the edit
    private void cancel() {

        // show a confirmation dialog
        Optional<ButtonType> buttonType = SceneManager.getInstance().showDialog(Alert.AlertType.CONFIRMATION, bundle.getString("cancel"), bundle.getString("notice.cancel"), null);

        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            // Reset the fields to the original data
            edit(false);
            displayData();
            lbPhoneErr.setText("");
            lbEmailErr.setText("");
            lbFullNameErr.setText("");
        }
    }

    // save the data
    private void save() {
        edit(false);
        // check if the data is changed
        newData = new AccountModel(tfUsername.getText(), tfPhone.getText(), tfEmail.getText(), rootData.getHash_password());
        if (rootData.isSame(newData)) {
            return;
        }

        // show a confirmation dialog
        Optional<ButtonType> button = SceneManager.getInstance().showDialog(Alert.AlertType.CONFIRMATION, bundle.getString("save"), bundle.getString("notice.save"), null);
        if (button.isPresent() && button.get() == ButtonType.OK) {

            if (!checkingData()) {
                displayData();
                return;
            }

            // create a request to update user information
            JSONObject request = new JSONObject();
            request.put("user_id", UserSession.getInstance().getUserId());
            request.put("username", newData.getFullName());
            request.put("phone", newData.getPhone());
            request.put("email", newData.getEmail());

            JSONObject respond = accountServices.updateUser(request);
            try {
                // parse the response
                String status, message;
                status = respond.getString("status");
                message = respond.getString("message");

                if (status.equals("failed")) {
                    SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("save"), bundle.getString("error"), message);
                    displayData();
                } else {
                    // update the data
                    rootData.setFullName(newData.getFullName());
                    rootData.setPhone(newData.getPhone());
                    rootData.setEmail(newData.getEmail());

                    // show a success message
                    SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("save"), null, bundle.getString("notice.userModifySuccess"));
                }
            } catch (JSONException e) {
                SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("save"), bundle.getString("error"), bundle.getString("notice.savingFailed"));
            }
        }
    }

    // check if the data is valid
    private boolean checkingData() {
        boolean flag = true;

        // kiểm tra tên người dùng
        if (newData.getFullName().isBlank()) {
            flag = false;
            lbFullNameErr.setText(bundle.getString("error.username"));
        } else {
            lbFullNameErr.setText(" ");
        }

        // kiểm tra số điện thoại
        if (newData.getPhone().isBlank()) {
            flag = false;
            lbPhoneErr.setText(bundle.getString("error.phone1"));
        } else if (!phoneNumberCheck(newData.getPhone())) {
            flag = false;
            lbPhoneErr.setText(bundle.getString("error.phone2"));
        } else {
            lbPhoneErr.setText(" ");
        }

        // kiêm tra email
        if (newData.getEmail().isBlank()) {
            flag = false;
            lbEmailErr.setText(bundle.getString("error.email1"));
        } else if (!emailCheck(newData.getEmail())) {
            flag = false;
            lbEmailErr.setText(bundle.getString("error.email2"));
        } else {
            lbEmailErr.setText(" ");
        }

        return flag;
    }

    // set editable
    private void edit(boolean isEdited) {
        btEdit.setDisable(isEdited);
        btSave.setDisable(!isEdited);
        btCancel.setDisable(!isEdited);

        tfUsername.setEditable(isEdited);
        tfPhone.setEditable(isEdited);
        tfEmail.setEditable(isEdited);
        if (isEdited) {
            tfUsername.setStyle("-fx-border-color: #0000C6");
            tfPhone.setStyle("-fx-border-color: #0000C6");
            tfEmail.setStyle("-fx-border-color: #0000C6");
        } else {
            tfUsername.setStyle("-fx-border-color: #FFFFFF");
            tfPhone.setStyle("-fx-border-color: #FFFFFF");
            tfEmail.setStyle("-fx-border-color: #FFFFFF");
        }
    }
}
