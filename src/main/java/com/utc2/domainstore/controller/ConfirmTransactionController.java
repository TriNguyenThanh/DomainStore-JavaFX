package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.BillViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.ITransactionService;
import com.utc2.domainstore.service.TransactionService;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import com.utc2.domainstore.view.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConfirmTransactionController implements Initializable {
    private ResourceBundle bundle;

    @FXML
    private Button btAdd, btRemove, btEdit;
    @FXML
    private TableView<BillViewModel> tableView;
    @FXML
    private TableColumn<BillViewModel, String> colID;
    @FXML
    private TableColumn<BillViewModel, LocalDate> colDate;
    @FXML
    private TableColumn<BillViewModel, STATUS> colStatus;
    @FXML
    private TableColumn<BillViewModel, Integer> colPrice;

    @FXML
    private TextField tfSearch;

    @FXML
    private void onHandleButton() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        initTable();
    }

    private void initTable() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        colDate.setCellFactory(LocalDateCellFactory.forTableColumn());

        ObservableList<BillViewModel> billObservableList = FXCollections.observableArrayList(getData());
        tableView.setItems(billObservableList);
    }

    private List<BillViewModel> getData() {
        List<BillViewModel> bills = new ArrayList<>();

        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());

        ITransactionService transactionService = new TransactionService();
        JSONObject respond = transactionService.getAllTransaction();
        JSONArray list = respond.getJSONArray("transactions");

        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            STATUS status = STATUS.valueOf(jsonObject.get("status").toString());
            if (status != STATUS.PENDINGCONFIRM) {
                continue;
            }
            String id = jsonObject.getString("id");
            LocalDate date = LocalDate.parse(jsonObject.optString("date"));
            int price = jsonObject.getInt("total_price");

            bills.add(new BillViewModel(id, date, status, price));
        }

        return bills;
    }
}
