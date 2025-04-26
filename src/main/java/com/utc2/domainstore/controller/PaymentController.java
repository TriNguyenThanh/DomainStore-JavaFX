package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.PaymentViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.IPaymentService;
import com.utc2.domainstore.service.PaymentService;
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
import java.time.LocalDate;
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
    private TableColumn<PaymentViewModel, Integer> colPayID;
    @FXML
    private TableColumn<PaymentViewModel, STATUS> colStatus;
    @FXML
    private TableColumn<PaymentViewModel, Date> colDate;
    @FXML
    private TableColumn<PaymentViewModel, String> colMethod;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        paymentList = getPaymentList();
        initTable();
    }

    private void initTable() {
        colBillID.setCellValueFactory(new PropertyValueFactory<>("billID"));
        colPayID.setCellValueFactory(new PropertyValueFactory<>("paymentID"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("method"));

        ObservableList<PaymentViewModel> paymentList = FXCollections.observableArrayList(getPaymentList());
        table.setItems(paymentList);
    }

    private List<PaymentViewModel> getPaymentList() {
        List<PaymentViewModel> data = new ArrayList<>();
        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());

        IPaymentService paymentService = new PaymentService();
        JSONObject respond = paymentService.getUserPaymentHistory(request);
        JSONArray array = respond.getJSONArray("paymentHistory");

        for (Object o : array) {
            JSONObject payment = (JSONObject) o;
            int id = payment.getInt("payment_id");
            String ts_id = payment.getString("transaction_id");
            String method = payment.get("method").toString();
            String date = payment.get("date").toString();
            String status = payment.get("status").toString();

            data.add(new PaymentViewModel(ts_id, id, method, STATUS.valueOf(status), LocalDate.parse(date)));
        }

        return data;
    }
}
