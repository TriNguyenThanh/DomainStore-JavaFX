package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.entity.view.ACCOUNT_STATUS;
import com.utc2.domainstore.entity.view.UserModel;
import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.service.IAccount;
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
    private Button btAdd, btRemove, btEdit;

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
    private void onHandleButton(ActionEvent e) {
        if (e.getSource() == btAdd) {
            // Handle add user logic
        } else if (e.getSource() == btRemove) {
            // Handle remove user logic
            handleRemoveUser();
        } else if (e.getSource() == btEdit) {
            // Handle edit user logic
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        data = getData();
        initTable();
    }

    private void initTable() {
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
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // So sánh với tên người dùng
                String lowerCaseFilter = newValue.toLowerCase();
                return user.getName().toLowerCase().contains(lowerCaseFilter) ||
                        user.getPhone().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Đặt nguồn dữ liệu cho TableView
        table.setItems(filteredData);
    }

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

            UserModel userModel = new UserModel(id, username, phone, email, personalId, role, status);

            newData.add(userModel);
        }
        return newData;
    }

    private void handleAddUser() {
        // Logic to add a new user
        // Open dialog to add a new user
        // Logic to add user
        // After adding, refresh the table
    }

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
            JSONObject request = new JSONObject();
            request.put("user_id", selectedItem.getID());
            IAccount accountService = new AccountServices();
            JSONObject response = accountService.lockedAccount(request);

            selectedItem.setStatus(ACCOUNT_STATUS.LOCKED);
        }
        table.refresh();
    }

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
            UserModel selectedUser = selectedUsers.get(0);
            // Open edit dialog with selected user data
            // Logic to update the user in the list
        }
    }
}
