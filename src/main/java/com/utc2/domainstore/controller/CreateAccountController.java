package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.entity.view.Method;
import com.utc2.domainstore.entity.view.UserModel;
import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.service.IAccount;
import com.utc2.domainstore.service.IRegister;
import com.utc2.domainstore.service.RegisterServices;
import com.utc2.domainstore.view.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.utc2.domainstore.utils.CheckingUtils.*;

public class CreateAccountController implements Initializable {
    private ResourceBundle bundle;
    private MainController mainController;
    private UserModel data;
    private UserModel newData = new UserModel();
    private IAccount accountServices = new AccountServices();
    private Method method;

    // FXML
    @FXML
    private Button btCancel, btSave, btBack, btChangePassword;
    @FXML
    private ComboBox<RoleEnum> cbRole;
    @FXML
    private TextField tfUsername, tfPhone, tfEmail, tfPsID, tfPass;
    @FXML
    private Label lbFullNameErr, lbPhoneErr, lbEmailErr, lbPsIDErr, lbPassErr;

    @FXML
    private void handleButtonOnAction(ActionEvent e) {
        if (e.getSource() == btCancel) {
            resetAccountForm();
        } else if (e.getSource() == btSave) {
            saveAccountForm();
        } else if (e.getSource() == btBack) {
            backToMainScene();
        } else if (e.getSource() == btChangePassword) {
            tfPass.setDisable(false);
            tfPass.setText("pass123456@");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        cbRole.getItems().addAll(List.<RoleEnum>of(RoleEnum.user, RoleEnum.admin));
        cbRole.setValue(RoleEnum.user);
    }

    public void setMethod(Method method) {
        this.method = method;

        // hide change password button
        if (method == Method.ADD) {
            btChangePassword.setDisable(true);
        } else if (method == Method.UPDATE) {
            tfPass.setDisable(true);
            tfPass.setEditable(false);
        }
    }

    public void setUserModel(UserModel accountModel) {
        this.data = accountModel;
        resetAccountForm();
    }

    // back to user management scene
    private void backToMainScene() {
        MainController.getInstance().load("/fxml/user_manager.fxml");
    }

    // set form to data and update to database
    private void saveAccountForm() {
        // set form to newData
        newData.setName(tfUsername.getText());
        newData.setPhone(tfPhone.getText());
        newData.setEmail(tfEmail.getText());
        newData.setPsID(tfPsID.getText());
        newData.setRole(cbRole.getValue());
        newData.setPassword(tfPass.getText());

        if (method == Method.UPDATE && newData.getID() == UserSession.getInstance().getUserId()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Selection");
            alert.setHeaderText(null);
            alert.setContentText(bundle.getString("error.modifyYourRole"));
            alert.showAndWait();
            return;
        }

        // kiểm tra dữ liệu nhập vào
        if (checkingData()) {
            // luu vao database
            JSONObject request = new JSONObject();
            request.put("user_id", newData.getID());
            request.put("username", tfUsername.getText());
            request.put("phone", tfPhone.getText());
            request.put("email", tfEmail.getText());
            request.put("password", tfPass.getText());
            request.put("personal_id", tfPsID.getText());
            request.put("role", cbRole.getValue().toString());

            JSONObject response = null;
            if (method == Method.ADD) {
                IRegister registerServices = new RegisterServices();
                response = registerServices.addToDB(request);
            } else if (method == Method.UPDATE) {
                if (!newData.equals(data)) {
                    // cập nhật thông tin người dùng
                    response = accountServices.updateUser(request);
                }
                if (!tfPass.isDisable()) {
                    // cập nhật mật khẩu
                    response = accountServices.updateUserPassword(request);

                    // hiển thị thông báo thành công
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(bundle.getString("notice"));
                    alert.setHeaderText(null);
                    alert.setContentText(bundle.getString("notice.updatePassSuccess"));
                    alert.showAndWait();
                }
            }

            if (response == null) {
                System.out.println("Error: response is null");
            } else if (response.getString("status").equals("failed")) {
                // hiển thị thông báo lỗi
                String message = response.getString("message");
                if (message.equals("Phone number already exists.")) {
                    lbPhoneErr.setText(bundle.getString("error.phone3"));
                } else if (message.equals("Email already exists.")) {
                    lbEmailErr.setText(bundle.getString("error.email3"));
                } else if (message.equals("CCCD already exists.")) {
                    lbPsIDErr.setText(bundle.getString("error.psIDErr3"));
                }
            } else if (response.getString("status").equals("success")) {
                // hiển thị thông báo thành công
                System.out.println(response.getString("message"));
            }

            // trở về giao diện quản lý người dùng
            backToMainScene();
        }
    }

    private void resetAccountForm() {
        // đặt lại dữ liệu ban đầu
        newData.copy(data);

        // đặt trạng thái của các trường nhập liệu
        tfUsername.setText(newData.getName());
        tfPhone.setText(newData.getPhone());
        tfEmail.setText(newData.getEmail());
        tfPsID.setText(newData.getPsID());
        tfPass.setText(newData.getPassword());
        cbRole.setValue(newData.getRole());
        lbFullNameErr.setText(" ");
        lbPhoneErr.setText(" ");
        lbEmailErr.setText(" ");
        lbPsIDErr.setText(" ");
        lbPassErr.setText(" ");

        // đặt lại trạng thái của trường mật khẩu
        if (method == Method.UPDATE) {
            tfPass.setDisable(true);
            tfPass.setEditable(false);
        }
    }

    // kiểm tra dữ liệu nhập vào
    private boolean checkingData() {
        boolean flag = true;

        // kiểm tra tên người dùng
        if (newData.getName().isBlank()) {
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

        // kiểm tra số CCCD
        if (newData.getPsID().isBlank()) {
            flag = false;
            lbPsIDErr.setText(bundle.getString("error.psIDErr1"));
        } else if (!personalIDCheck(newData.getPsID())) {
            flag = false;
            lbPsIDErr.setText(bundle.getString("error.psIDErr2"));
        } else {
            lbPsIDErr.setText(" ");
        }

        // kiểm tra mật khẩu
        if (method == Method.ADD) {
            if (newData.getPassword().isBlank()) {
                flag = false;
                lbPassErr.setText(bundle.getString("error.password1"));
            } else if (!passwordCheck(newData.getPassword())) {
                flag = false;
                lbPassErr.setText(bundle.getString("error.password2"));
            } else {
                lbPassErr.setText(" ");
            }
        }

        return flag;
    }
}
