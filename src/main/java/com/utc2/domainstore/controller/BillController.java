package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.BillViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.ITransactionService;
import com.utc2.domainstore.service.TransactionService;
import com.utc2.domainstore.view.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BillController implements Initializable {
    private ResourceBundle bundle;
    private List<BillViewModel> data;

    @FXML
    private TableView<BillViewModel> tableView;
    @FXML
    private TableColumn<BillViewModel, Integer> colID;
    @FXML
    private TableColumn<BillViewModel, Date> colDate;
    @FXML
    private TableColumn<BillViewModel, STATUS> colStatus;
    @FXML
    private TableColumn<BillViewModel, Integer> colPrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        data = getData();
        initTable();
    }

    private void initTable() {
        tableView = new TableView<>();
        colID.setCellValueFactory(new PropertyValueFactory<>(bundle.getString("billID")));
        ObservableList<BillViewModel> billObservableList = FXCollections.observableArrayList(data);
        tableView.setItems(billObservableList);
    }

    private List<BillViewModel> getData() {
        List<BillViewModel> bills = new ArrayList<>();

        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());

        ITransactionService transactionService = new TransactionService();
        JSONObject respond = transactionService.getAllUserTransaction(request);
        JSONArray list = respond.getJSONArray("transactions");

        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            String id = jsonObject.getString("id");
            Date date = Date.valueOf(jsonObject.get("date").toString()); //jsonObject.get("date");
            STATUS status = STATUS.valueOf(jsonObject.get("status").toString());
            int price = jsonObject.getInt("total_price");

            bills.add(new BillViewModel(id, date, status, price));
        }

        return bills;
    }
}