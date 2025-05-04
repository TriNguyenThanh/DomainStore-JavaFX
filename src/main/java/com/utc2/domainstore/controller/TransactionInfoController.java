package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.TransactionStatusEnum;
import com.utc2.domainstore.entity.view.*;
import com.utc2.domainstore.service.*;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TransactionInfoController implements Initializable, PaymentListener {
    private ResourceBundle bundle;
    private BillViewModel billViewModel;
    private AccountModel accountModel;
    private List<DomainViewModel> domainList;
    private PaymentViewModel paymentViewModel;
    private METHOD method;
    private TransactionService transactionService = new TransactionService();

    @FXML
    private Label lbUsername, lbPhone, lbEmail, lbBillID, lbDate, lbTotal;
    @FXML
    private Label lbStatus, lbPaymentID, lbMethod, lbPaymentDate;
    @FXML
    private Button btExport, btPay, btAccept, btCancel;
    @FXML
    private HBox btContainer;
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
            hanldleExport();
        } else if (e.getSource() == btPay) {
            // pay
            handlePay();
        } else if (e.getSource() == btAccept) {
            // accept
            handleAccept();
        } else if (e.getSource() == btCancel) {
            // cancel
            handleCancel();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        accountModel = getAccountModel();
    }

    public void setMethod(METHOD method) {
        this.method = method;
        List<Button> buttons = new ArrayList<>();
        if (method == METHOD.PAY) {
            // Set up for payment
            buttons.add(btPay);
            buttons.add(btCancel);
        } else if (method == METHOD.CONFIRM) {
            // Set up for confirmation
            buttons.add(btAccept);
            buttons.add(btCancel);
        } else if (method == METHOD.REVIEW) {
            // Set up for review
            if (billViewModel.getStatus() == STATUS.COMPLETED) {
                buttons.add(btExport);
                paymentViewModel = getPaymentViewModel();
            }

        }
        btContainer.getChildren().clear();
        btContainer.getChildren().addAll(buttons);
        displayBillInfo();
    }

    private void handlePay() throws IOException {
        // Handle the payment return
        JSONObject request = new JSONObject();
        request.put("transactionId", billViewModel.getId());
        request.put("total", billViewModel.getPrice());

        PaymentService paymentService = new PaymentService();
        paymentService.setListener(this);
        boolean success = paymentService.createPayment(request);
        System.out.println("Open payment website: " + success);
    }

    private void handleAccept() {
        // Handle the accept action
        transactionService.updateTransactionStatus(billViewModel.getId(), TransactionStatusEnum.PENDINGPAYMENT);
        System.out.println("Transaction accepted: " + billViewModel.getId());
        // close the window
        ((Stage) btAccept.getScene().getWindow()).close();
    }

    private void handleCancel() {
        // Handle the cancel action
        transactionService.updateTransactionStatus(billViewModel.getId(), TransactionStatusEnum.CANCELLED);
        System.out.println("Transaction canceled: " + billViewModel.getId());
        ((Stage) btCancel.getScene().getWindow()).close();
    }

    private void hanldleExport() {
        // Handle the export action
        System.out.println("Exporting transaction: " + billViewModel.getId());
        // Implement export logic here
        GenerateService generateService = new GenerateService();
        generateService.generateInvoicePDF(billViewModel.getId());
    }

    private void displayBillInfo() {
        lbUsername.setText(accountModel.getFullName());
        lbPhone.setText(accountModel.getPhone());
        lbEmail.setText(accountModel.getEmail());

        lbBillID.setText(String.valueOf(billViewModel.getId()));
        lbDate.setText(billViewModel.getDate().format(ConfigManager.getInstance().getDateTimeFormatter()));
        lbTotal.setText(bundle.getString("total") + ": " + ConfigManager.getInstance().getNumberFormatter().format(billViewModel.getPrice()));
        lbStatus.setText(String.valueOf(billViewModel.getStatus()));

        if (paymentViewModel != null) {
            lbPaymentID.setText(String.valueOf(paymentViewModel.getPaymentID()));
            lbMethod.setText(paymentViewModel.getMethod());
            lbPaymentDate.setText(paymentViewModel.getPaymentDate().format(ConfigManager.getInstance().getDateTimeFormatter()));
        } else {
            lbPaymentID.setText("");
            lbMethod.setText("");
            lbPaymentDate.setText("");
        }
    }

    private void initTable() {
        colDomainName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDomainPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDomainStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDomainYears.setCellValueFactory(new PropertyValueFactory<>("years"));

        colDomainPrice.setCellFactory(MoneyCellFactory.forTableColumn());

        ObservableList<DomainViewModel> observableList = FXCollections.observableArrayList(domainList);
        table.setItems(observableList);
    }

    public void setBillViewModel(BillViewModel billViewModel) {
        this.billViewModel = billViewModel;
        domainList = getDomainList();
        initTable();
        displayBillInfo();
    }

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

    private List<DomainViewModel> getDomainList() {
        List<DomainViewModel> domains = new ArrayList<>();

        JSONObject request = new JSONObject();
        request.put("transaction_id", billViewModel.getId());

        ITransactionService transactionService = new TransactionService();
        JSONObject respond = transactionService.getTransactionInfomation(request);
        JSONArray list = respond.getJSONArray("domains");

        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            String name = jsonObject.getString("name");
            STATUS status = STATUS.valueOf(jsonObject.get("status").toString().toUpperCase());
            int price = jsonObject.getInt("price");
            int years = jsonObject.getInt("years");

            domains.add(new DomainViewModel(name, status, price, years, null));
        }

        return domains;
    }

    private PaymentViewModel getPaymentViewModel() {
        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());

        IPaymentService paymentService = new PaymentService();
        JSONObject respond = paymentService.getUserPaymentHistory(request);
        JSONArray array = respond.getJSONArray("paymentHistory");

        for (Object o : array) {
            JSONObject payment = (JSONObject) o;
            String id = payment.getString("payment_id");
            String ts_id = payment.getString("transaction_id");
            String method = payment.get("method").toString();
            String date = payment.get("date").toString();
            String status = payment.get("status").toString();

            if (ts_id.equals(billViewModel.getId())) {
                return new PaymentViewModel(ts_id, id, method, STATUS.valueOf(status), LocalDate.parse(date));
            }
        }
        return null;
    }

    @Override
    public void onPaymentProcessed(Map<String, String> paymentResult) {
        Platform.runLater(() -> {
            if (paymentResult.get("status").equals("success")) {
                transactionService.updateTransactionStatus(billViewModel.getId(), TransactionStatusEnum.COMPLETED);
                billViewModel.setStatus(STATUS.COMPLETED);
                paymentViewModel = getPaymentViewModel();
                setMethod(METHOD.REVIEW);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(bundle.getString("error"));
                alert.setHeaderText(null);
                alert.setContentText(bundle.getString("notice.paymentFailed"));
                alert.showAndWait();
            }
        });
    }
}
