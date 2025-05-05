package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.PaymentViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.IPaymentService;
import com.utc2.domainstore.service.PaymentService;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.utils.StatusCellFactory;
import com.utc2.domainstore.view.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {
    private ResourceBundle bundle;
    private Integer userId = 0;

    @FXML
    private TableView<PaymentViewModel> table;
    @FXML
    private TableColumn<PaymentViewModel, String> colBillID;
    @FXML
    private TableColumn<PaymentViewModel, String> colPayID;
    @FXML
    private TableColumn<PaymentViewModel, Integer> colPrice;
    @FXML
    private TableColumn<PaymentViewModel, STATUS> colStatus;
    @FXML
    private TableColumn<PaymentViewModel, LocalDate> colDate;
    @FXML
    private TableColumn<PaymentViewModel, String> colMethod;
    @FXML
    private TextField tfSearch;
    @FXML
    private Button btBack;
    @FXML
    private AnchorPane pnContainer;

    @FXML
    private void onHandleButton(ActionEvent e) {
        if (e.getSource() == btBack) {
            // quay lại
            handleBackButton();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        initTable();
        // remove button back
        btBack.setVisible(false);
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
        // update table
        btBack.setVisible(true);
        updateTable();
    }

    private void initTable() {
        colBillID.setCellValueFactory(new PropertyValueFactory<>("billID"));
        colPayID.setCellValueFactory(new PropertyValueFactory<>("paymentID"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("method"));

        colPrice.setCellFactory(MoneyCellFactory.forTableColumn());
        colDate.setCellFactory(LocalDateCellFactory.forTableColumn());
        colStatus.setCellFactory(StatusCellFactory.forTableColumn());

        // set text when no data
        table.setPlaceholder(new Label(bundle.getString("placeHolder.tableEmpty")));

        updateTable();
    }

    private void updateTable() {
        ObservableList<PaymentViewModel> paymentList = FXCollections.observableArrayList(getPaymentList());
        // set filter
        FilteredList<PaymentViewModel> filteredList = new FilteredList<>(paymentList, p -> true);
        tfSearch.textProperty().addListener(((observable, oldValue, newValue) -> updateFilteredList(filteredList)));
        // set data
        table.setItems(filteredList);
    }

    private void handleBackButton() {
        // quay lại
        MainController.getInstance().load("/fxml/user_manager.fxml");
    }

    private List<PaymentViewModel> getPaymentList() {
        List<PaymentViewModel> data = new ArrayList<>();
        JSONObject request = new JSONObject();
        if (userId == 0) {
            request.put("user_id", UserSession.getInstance().getUserId());
        } else {
            request.put("user_id", userId);
        }
        IPaymentService paymentService = new PaymentService();
        JSONObject respond = paymentService.getUserPaymentHistory(request);
        JSONArray array = respond.getJSONArray("paymentHistory");

        for (Object o : array) {
            JSONObject payment = (JSONObject) o;
            String id = payment.getString("payment_id");
            String ts_id = payment.getString("transaction_id");
            Integer price = payment.getInt("total");
            String method = payment.get("method").toString();
            String date = payment.get("date").toString();
            String status = payment.get("status").toString();

            data.add(new PaymentViewModel(ts_id, id, method, STATUS.valueOf(status), LocalDate.parse(date), price));
        }

        return data;
    }

    // update filtered list
    public void updateFilteredList(FilteredList<PaymentViewModel> filteredList) {
        //get text from search bar
        String searchText = tfSearch.getText();
        // check if text is empty
        filteredList.setPredicate(PaymentViewModel -> {
            // if text is empty, show all data
            if (searchText.isEmpty()) {
                return true;
            }
            // check if id or name contains text
            if (PaymentViewModel.getBillID().toLowerCase().contains(searchText.toLowerCase()) ||
                    PaymentViewModel.getPaymentID().toLowerCase().contains(searchText.toLowerCase())) {
                return true;
            }
            return false;
        });
    }
}
