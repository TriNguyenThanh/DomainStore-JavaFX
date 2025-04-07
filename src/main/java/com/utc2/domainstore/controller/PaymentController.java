package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.PaymentViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.json.JSONObject;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {
    private ResourceBundle bundle;
    private List<PaymentViewModel> paymentList;

    @FXML
    private TableView<PaymentViewModel> table;
    @FXML
    private TableColumn<PaymentViewModel, String> colBillID;
    @FXML
    private TableColumn<PaymentViewModel, String> colPayID;
    @FXML
    private TableColumn<PaymentViewModel, STATUS> colStatus;
    @FXML
    private TableColumn<PaymentViewModel, Date> colDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        paymentList = getPaymentList();
        initTable();
    }

    private void initTable() {
    }

    private List<PaymentViewModel> getPaymentList() {
        List<PaymentViewModel> data = new ArrayList<>();
        JSONObject request = new JSONObject();
//        request.put("")

        return null;
    }
}
