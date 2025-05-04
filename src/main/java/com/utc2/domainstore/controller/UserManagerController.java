package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.entity.view.ACCOUNT_STATUS;
import com.utc2.domainstore.entity.view.METHOD;
import com.utc2.domainstore.entity.view.UserModel;
import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.service.IAccount;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserManagerController implements Initializable {
    private ResourceBundle bundle;
    private List<UserModel> data;

    @FXML
    private Button btAdd, btRemove, btEdit, btActive, btPaymentHistory;
    @FXML
    private TableView<UserModel> table;
    @FXML
    private TableColumn<UserModel, Integer> colID;
    @FXML
    private TableColumn<UserModel, String> colUsername;
    @FXML
    private TableColumn<UserModel, String> colPhone;
    @FXML
    private TableColumn<UserModel, String> colEmail;
    @FXML
    private TableColumn<UserModel, String> colPsID;
    @FXML
    private TableColumn<UserModel, String> colRole;
    @FXML
    private TableColumn<UserModel, ACCOUNT_STATUS> colStatus;
    @FXML
    private TextField tfSearch;
    @FXML
    private ComboBox<String> cbStatus, cbRole;

    @FXML
    private void onHandleButton(ActionEvent e) {
        if (e.getSource() == btAdd) {
            // Handle add user logic
            handleAddUser();
        } else if (e.getSource() == btRemove) {
            // Handle remove user logic
            handleRemoveUser();
        } else if (e.getSource() == btEdit) {
            // Handle edit user logic
            handleEditUser();
        } else if (e.getSource() == btActive) {
            // Handle active user logic
            // Logic to activate selected users
            // After activating, refresh the table
            handleActiveUser();
        } else if (e.getSource() == btPaymentHistory) {
            // open payment history dialog
            handlePaymentHistory();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        data = getData();
        initTable();
    }

    private void initTable() {
        // Khởi tạo giá trị cho comboBox
        cbStatus.getItems().addAll("", "ACTIVE", "LOCKED");
        cbRole.getItems().addAll("", "USER", "ADMIN");

        // Khởi tạo các cột của bảng
        colID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPsID.setCellValueFactory(new PropertyValueFactory<>("psID"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Tạo một ObservableList từ danh sách dữ liệu
        ObservableList<UserModel> userModelObservableList = FXCollections.observableArrayList(data);
        FilteredList<UserModel> filteredData = new FilteredList<>(userModelObservableList, p -> true);

        // Đặt listener cho TextField để lọc dữ liệu
        // Lắng nghe thay đổi từ các điều kiện lọc
        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));
        cbStatus.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));
        cbRole.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));

        // Đặt nguồn dữ liệu cho TableView
        table.setItems(filteredData);
    }

    // Lấy dữ liệu từ server
    private List<UserModel> getData() {
        List<UserModel> newData = new ArrayList<>();

        IAccount accountService = new AccountServices();
        JSONObject respond = accountService.getAllUserAccount();
        JSONArray array = respond.getJSONArray("users");

        for (Object o : array) {
            JSONObject jsonObject = (JSONObject) o;
            int id = jsonObject.getInt("user_id");
            String username = jsonObject.getString("username");
            String phone = jsonObject.getString("phone");
            String email = jsonObject.getString("email");
            String personalId = jsonObject.getString("personal_id");
            RoleEnum role = RoleEnum.valueOf(jsonObject.get("role").toString());
            ACCOUNT_STATUS status = jsonObject.getBoolean("is_deleted") ? ACCOUNT_STATUS.LOCKED : ACCOUNT_STATUS.ACTIVE;

            UserModel userModel = new UserModel(id, username, phone, email, personalId, role, status, "");

            newData.add(userModel);
        }
        return newData;
    }

    // thêm người dùng
    private void handleAddUser() {
        // Logic to add a new user
        // Open dialog to add a new user
        // Logic to add user
        // After adding, refresh the table
        CreateAccountController createAccountController = MainController.getInstance().load("/fxml/createAccount.fxml").getController();
        createAccountController.setMethod(METHOD.ADD);
    }

    // Khóa người dùng
    private void handleRemoveUser() {
        // Logic to remove selected users
        UserModel selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to remove.");
            alert.showAndWait();
        } else {
            // Remove selected users from the list
            if (selectedItem.getID() == UserSession.getInstance().getUserId()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Selection");
                alert.setHeaderText(null);
                alert.setContentText(bundle.getString("error.lockYourSelf"));
                alert.showAndWait();
                return;
            }

            JSONObject request = new JSONObject();
            request.put("user_id", selectedItem.getID());
            IAccount accountService = new AccountServices();
            JSONObject response = accountService.lockedAccount(request);

            selectedItem.setStatus(ACCOUNT_STATUS.LOCKED);
        }
        table.refresh();
    }

    // Chỉnh sửa người dùng
    private void handleEditUser() {
        // Logic to edit selected user
        ObservableList<UserModel> selectedUsers = table.getSelectionModel().getSelectedItems();
        if (selectedUsers.size() != 1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select exactly one user to edit.");
            alert.showAndWait();
        } else {
            UserModel selectedUser = selectedUsers.getFirst();
            selectedUser.setPassword("********");
            // Open edit dialog with selected user data
            // Logic to update the user in the list
            CreateAccountController createAccountController = MainController.getInstance().load("/fxml/createAccount.fxml").getController();
            createAccountController.setMethod(METHOD.UPDATE);
            createAccountController.setUserModel(selectedUser);
        }
    }

    // Kích hoạt người dùng
    private void handleActiveUser() {
        // Logic to activate selected users
        ObservableList<UserModel> selectedUsers = table.getSelectionModel().getSelectedItems();
        if (selectedUsers.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select at least one user to activate.");
            alert.showAndWait();
        } else {
            for (UserModel user : selectedUsers) {
                // Logic to activate the user
                JSONObject request = new JSONObject();
                request.put("user_id", user.getID());
                request.put("full_name", user.getName());
                request.put("phone", user.getPhone());
                request.put("email", user.getEmail());
                request.put("ps_id", user.getPsID());
                request.put("is_deleted", false);
                IAccount accountService = new AccountServices();
                JSONObject response = accountService.updateUser(request);

                user.setStatus(ACCOUNT_STATUS.ACTIVE);
            }
        }
        table.refresh();
    }

    // Xem lịch sử thanh toán
    private void handlePaymentHistory() {
        // Logic to open payment history dialog
        UserModel selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            SceneManager.getInstance().showDialog(Alert.AlertType.WARNING, null, null, bundle.getString("error.noSelect"));
        } else {
            // Open payment history dialog
            PaymentController paymentController = MainController.getInstance().load("/fxml/payment.fxml").getController();
            paymentController.setUserId(selectedItem.getID());
        }
    }

    private void updateFilter(FilteredList<UserModel> filteredData) {
        String searchText = tfSearch.getText().toLowerCase();
        String selectedStatus = cbStatus.getSelectionModel().getSelectedItem();
        String selectedRole = cbRole.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(user -> {
            // 1. Lọc theo từ khóa
            boolean matchesSearch = (searchText.isBlank()) ||
                    user.getName().toLowerCase().contains(searchText) ||
                    user.getPhone().toLowerCase().contains(searchText);

            // 2. Lọc theo trạng thái
            boolean matchesStatus = (selectedStatus == null || selectedStatus.isEmpty()) ||
                    user.getStatus().toString().equalsIgnoreCase(selectedStatus);

            // 3. Lọc theo vai trò
            boolean matchesRole = (selectedRole == null || selectedRole.isEmpty()) ||
                    user.getRole().toString().equalsIgnoreCase(selectedRole);

            // Kết hợp tất cả điều kiện
            return matchesSearch && matchesStatus && matchesRole;
        });
    }
}
