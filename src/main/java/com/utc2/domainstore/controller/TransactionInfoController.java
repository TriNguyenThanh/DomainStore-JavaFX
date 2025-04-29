package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.AccountModel;
import com.utc2.domainstore.entity.view.BillViewModel;
import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.*;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionInfoController implements Initializable {
    private ResourceBundle bundle;
    private BillViewModel billViewModel;
    private AccountModel accountModel;
    private List<DomainViewModel> domainList;

    private static final VnPayService vnPayService = new VnPayService();

    @FXML
    private Label lbUsername, lbPhone, lbEmail, lbBillID, lbDate, lbTotal;
    @FXML
    private Label lbStatus, lbPaymentID, lbMethod, lbPaymentDate;
    @FXML
    private AnchorPane imgQR;
    @FXML
    private Button btExport, btPay;

    @FXML
    private TableView<DomainViewModel> table;
    @FXML
    private TableColumn<DomainViewModel, String> colDomainName;
    @FXML
    private TableColumn<DomainViewModel, Integer> colDomainPrice;
    @FXML
    private TableColumn<DomainViewModel, STATUS> colDomainStatus;
    @FXML
    private TableColumn<DomainViewModel, Integer> colDomainYears;

    @FXML
    private void handleButtonOnAction(ActionEvent e) throws IOException {
        if (e.getSource() == btExport) {
            // export
        } else if (e.getSource() == btPay) {
            // pay
            pay();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        accountModel = getAccountModel();
    }

    private void pay() throws IOException {
        // Handle the payment return
        JSONObject request = new JSONObject();
        request.put("transactionId", billViewModel.getId());
        request.put("total", billViewModel.getPrice());

        IPaymentService paymentService = new PaymentService();
        boolean success = paymentService.createPayment(request);
        if (success) {
            // Handle successful payment
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString("payment"));
            alert.setHeaderText(null);
            alert.setContentText(bundle.getString("notice.paymentSuccess"));
            alert.showAndWait();

        } else {
            // Handle failed payment
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("payment"));
            alert.setHeaderText(null);
            alert.setContentText(bundle.getString("notice.paymentFailed"));
            alert.showAndWait();
        }

    }

    private void displayBillInfo() {
        lbUsername.setText(accountModel.getFullName());
        lbPhone.setText(accountModel.getPhone());
        lbEmail.setText(accountModel.getEmail());

        lbBillID.setText(String.valueOf(billViewModel.getId()));
        lbDate.setText(billViewModel.getDate().format(ConfigManager.getInstance().getFormatter()));
        lbTotal.setText(bundle.getString("total") + ": " + billViewModel.getPrice());
        lbStatus.setText(String.valueOf(billViewModel.getStatus()));
    }

    private void initTable() {
        JSONObject request = new JSONObject();
        request.put("transaction_id", billViewModel.getId());
        ITransactionService transactionServices = new TransactionService();
        JSONObject respond = transactionServices.getTransactionInfomation(request);
        JSONArray jsonArray = respond.getJSONArray("domains");

        domainList = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            DomainViewModel domainViewModel = new DomainViewModel();
            domainViewModel.setName(jsonObject.getString("name"));
            domainViewModel.setStatus(STATUS.valueOf(jsonObject.get("status").toString().toUpperCase()));
            domainViewModel.setPrice(jsonObject.getInt("price"));
            domainViewModel.setYears(jsonObject.getInt("years"));
            domainList.add(domainViewModel);
        }

        colDomainName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDomainPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDomainStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDomainYears.setCellValueFactory(new PropertyValueFactory<>("years"));

        ObservableList<DomainViewModel> observableList = FXCollections.observableArrayList(domainList);
        table.setItems(observableList);
    }

    public void setBillViewModel(BillViewModel billViewModel) {
        this.billViewModel = billViewModel;
        displayBillInfo();
        initTable();
    }

//    private

    private AccountModel getAccountModel() {
        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());

        IAccount accountServices = new AccountServices();
        JSONObject respond = accountServices.getUserInformation(request);

        String fullname = respond.getString("username");
        String phone = respond.getString("phone");
        String email = respond.getString("email");
        String psID = respond.getString("personal_id");
        String pass = respond.getString("password");

        return new AccountModel(fullname, phone, email, psID, pass);
    }
}
