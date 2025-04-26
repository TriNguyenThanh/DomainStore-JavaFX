package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.entity.view.UserModel;
import com.utc2.domainstore.service.AccountServices;
import com.utc2.domainstore.service.IAccount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private void onHandleButton(ActionEvent e) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        data = getData();
        initTable();
    }

    private void initTable() {
        colID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPsID.setCellValueFactory(new PropertyValueFactory<>("psID"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        ObservableList<UserModel> userModelObservableList = FXCollections.observableArrayList(data);
        table.setItems(userModelObservableList);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

            UserModel userModel = new UserModel(id, username, phone, email, personalId, role);

            newData.add(userModel);
        }
        return newData;
    }
}
