package com.utc2.domainstore.controller;

import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.utils.CheckingUtils;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {
    private ResourceBundle bundle;
    private AccountServices accountServices;
    private String rootEmail = "";
    private boolean isConfirm = false;
    private boolean isChecking = false;

    @FXML
    private Label lbNewErr, lbConfirmErr, lbPhoneErr, lbEmailErr;
    @FXML
    private PasswordField tfNew, tfConfirm;
    @FXML
    private TextField tfOTP, tfPhone, tfEmail;
    @FXML
    private CheckBox cbPass;
    @FXML
    private Button btCancel, btSave, btConfirm, btConfirm1, btCancel1;
    @FXML
    private AnchorPane otp_pane, password_pane, info_pane;

    @FXML
    private void handleButtonOnAction(ActionEvent e) {
        if (e.getSource() == btCancel1 || e.getSource() == btCancel) {
            ((Stage) btCancel.getScene().getWindow()).close();
        } else if (e.getSource() == btConfirm1) {
            checkInfo();
        } else if (e.getSource() == btConfirm) {
            checkOTP();
        } else if (e.getSource() == btSave) {
            changePassword();
        }
    }

    @FXML
    private void cbOnAction() {

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
        setAction();
    }

    private void setAction() {
        tfEmail.setOnAction(ActionEvent -> {
            if (!tfEmail.getText().isBlank() && !tfPhone.getText().isBlank()) {
                checkInfo();
            }
        });
        tfPhone.setOnAction(ActionEvent -> {
            if (!tfEmail.getText().isBlank() && !tfPhone.getText().isBlank()) {
                checkInfo();
            }
        });
        tfOTP.setOnAction(ActionEvent -> {
            if (!tfOTP.getText().isBlank()) {
                checkOTP();
            }
        });
        tfNew.setOnAction(ActionEvent -> {
            if (!tfNew.getText().isBlank() && !tfConfirm.getText().isBlank()) {
                changePassword();
            }
        });
        tfConfirm.setOnAction(ActionEvent -> {
            if (!tfNew.getText().isBlank() && !tfConfirm.getText().isBlank()) {
                changePassword();
            }
        });
    }

    public void setRootEmail(String email) {
        rootEmail = email;
        if (!rootEmail.isBlank()) {
            tfEmail.setText(rootEmail);
            tfEmail.setEditable(false);
        }
    }

    private void changePassword() {
        tfOnclick();
        boolean flag = true;

        if (tfNew.getText().isBlank()) {
            lbNewErr.setText(bundle.getString("error.newPasswordErr1"));
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
        request.put("email", rootEmail);
        request.put("password", tfNew.getText());

        JSONObject respond = accountServices.updatingNewPassWord(request);

        if (respond.get("status").equals("failed")) {
            SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, bundle.getString("error"), null, bundle.getString("notice.updatePassFailed"));
        } else {
            SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("notice"), null, bundle.getString("notice.updatePassSuccess"));
        }
        ((Stage) tfNew.getScene().getWindow()).close();
    }

    private void checkInfo() {
        if (isConfirm) {
            System.out.println("Button is already processing.");
            return;
        }
        isConfirm = true;
        btConfirm1.setDisable(true);
        btCancel1.setDisable(true);
        System.out.println("Button is disabled for processing.");

        String phone = tfPhone.getText();
        String email = tfEmail.getText();

        // Kiểm tra định dạng trước (thực hiện ngay trong JavaFX Thread)
        boolean hasError = false;

        if (phone.isBlank()) {
            lbPhoneErr.setText(bundle.getString("error.phone1"));
            hasError = true;
        } else if (!CheckingUtils.phoneNumberCheck(phone)) {
            lbPhoneErr.setText(bundle.getString("error.phone2"));
            hasError = true;
        } else {
            lbPhoneErr.setText("");
        }

        if (email.isBlank()) {
            lbEmailErr.setText(bundle.getString("error.email1"));
            hasError = true;
        } else if (!CheckingUtils.emailCheck(email)) {
            lbEmailErr.setText(bundle.getString("error.email2"));
            hasError = true;
        } else {
            lbEmailErr.setText("");
        }

        if (hasError) {
            isConfirm = false;
            btConfirm1.setDisable(false);
            btCancel1.setDisable(false);
            System.out.println("Validation failed, button re-enabled.");
            return;
        }

        // Tạo một Task để thực hiện truy vấn DB trong background
        Task<JSONObject> task = new Task<>() {
            @Override
            protected JSONObject call() {
                JSONObject request = new JSONObject();
                request.put("phone", phone);
                request.put("email", email);

                // Gọi dịch vụ liên quan đến database
                return accountServices.sendOtpToUser(request);
            }
        };

        // Xử lý kết quả trả về từ Task (chạy trên JavaFX Thread)
        task.setOnSucceeded(workerStateEvent -> {
            JSONObject response = task.getValue();
            if (response == null) {
                lbPhoneErr.setText(bundle.getString("error.phone4"));
                lbEmailErr.setText(bundle.getString("error.email4"));
            } else if ("success".equals(response.getString("status"))) {
                rootEmail = email;
                info_pane.setVisible(false);
            } else {
                lbPhoneErr.setText(bundle.getString("error.phone4"));
                lbEmailErr.setText(bundle.getString("error.email4"));
            }

            isConfirm = false;
            btConfirm1.setDisable(false);
            btCancel1.setDisable(false);
            System.out.println("Processing done, button re-enabled.");
        });

        // Xử lý lỗi khi task bị exception
        task.setOnFailed(workerStateEvent -> {
            lbPhoneErr.setText("Unexpected error.");
            lbEmailErr.setText("Unexpected error.");

            isConfirm = false;
            btConfirm1.setDisable(false);
            btCancel1.setDisable(false);
            System.out.println("Task failed, button re-enabled.");
            task.getException().printStackTrace(); // Ghi log lỗi
        });

        // Chạy task trong một luồng mới
        new Thread(task).start();
    }

    private void checkOTP() {
        if (isChecking) return;

        isChecking = true;
        btConfirm.setDisable(true);

        String otp = tfOTP.getText();
        String email = rootEmail;

        Task<JSONObject> task = new Task<>() {
            @Override
            protected JSONObject call() throws Exception {
                JSONObject request = new JSONObject();
                request.put("otp", otp);
                request.put("email", email);

                return accountServices.checkingOtp(request);
            }
        };

        task.setOnSucceeded(workerStateEvent -> {
            JSONObject response = task.getValue();

            if (response.getString("status").equals("success")) {
                otp_pane.setVisible(false);
            } else {
                SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("recovery_password"), null, bundle.getString("error.otpIncorrect"));
            }

            btConfirm.setDisable(false);
            isChecking = false;
        });

        new Thread(task).start();
    }
}
