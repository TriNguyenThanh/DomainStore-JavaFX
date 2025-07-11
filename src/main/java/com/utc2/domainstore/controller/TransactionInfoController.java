package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.database.TransactionStatusEnum;
import com.utc2.domainstore.entity.view.*;
import com.utc2.domainstore.service.*;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.utils.StatusCellFactory;
import com.utc2.domainstore.utils.YearCellFactory;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TransactionInfoController implements Initializable, PaymentListener {
    private ResourceBundle bundle;
    private BillViewModel billViewModel;
    private AccountModel accountModel;
    private PaymentViewModel paymentViewModel;
    private METHOD method;
    private TransactionService transactionService = new TransactionService();
    private PaymentService paymentService = new PaymentService();
    private ToggleGroup paymentMethod = new ToggleGroup();

    @FXML
    private Label lbUsername, lbPhone, lbEmail, lbBillID, lbDate, lbTotal, lbChange;
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
    private AnchorPane paymentPanel;
    @FXML
    private RadioButton rbVN_Pay, rbZalo_Pay, rbMoMo_Pay;

    @FXML
    private void handleButtonOnAction(ActionEvent e) throws IOException {
        if (e.getSource() == btExport) {
            // export
            handleExport();
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
        rbVN_Pay.setToggleGroup(paymentMethod);
        rbZalo_Pay.setToggleGroup(paymentMethod);
        rbMoMo_Pay.setToggleGroup(paymentMethod);
    }

    public void setMethod(METHOD method) {
        this.method = method;
        paymentViewModel = getPaymentViewModel();
        List<Button> buttons = new ArrayList<>();
        if (method == METHOD.PAY) {
            // Set up for payment
            buttons.add(btPay);
            buttons.add(btCancel);

            lbChange.setVisible(false);
            lbChange.setOnMouseClicked(ActionEvent -> {
                handleChange();
                lbChange.setVisible(false);
//                System.out.println("change payment method");
            });
        } else if (method == METHOD.CONFIRM) {
            // Set up for confirmation
            buttons.add(btAccept);
            buttons.add(btCancel);
            paymentPanel.setVisible(false);
        } else if (method == METHOD.REVIEW) {
            // Set up for review
            paymentPanel.setVisible(false);
            if (billViewModel.getStatus() == STATUS.COMPLETED) {
                buttons.add(btExport);
            }
        }
        btContainer.getChildren().clear();
        btContainer.getChildren().addAll(buttons);
        displayBillInfo();
    }

    private void handlePay() throws IOException {
        RadioButton rbPayment = checkPaymentMethod();
        String paymentmethod = "";

        if (rbPayment == null) {
            SceneManager.getInstance().showDialog(Alert.AlertType.WARNING, bundle.getString("warning"), null, bundle.getString("error.paymentMethod"));
            return;
        } else {
            lbChange.setVisible(true);
            for (Object o : paymentMethod.getToggles()) {
                RadioButton rb = (RadioButton) o;
                rb.setDisable(true);
            }
            if (rbPayment == rbVN_Pay) {
                paymentmethod = "VNPAY";
                rbVN_Pay.setDisable(false);
            } else if (rbPayment == rbZalo_Pay) {
                paymentmethod = "ZALOPAY";
                rbZalo_Pay.setDisable(false);
            } else if (rbPayment == rbMoMo_Pay) {
                paymentmethod = "MOMO";
                rbMoMo_Pay.setDisable(false);
            }
        }
        // Handle the payment return
        JSONObject request = new JSONObject();
        request.put("transactionId", billViewModel.getId());
        request.put("total", billViewModel.getPrice());
        request.put("paymentMethod", paymentmethod);

        PaymentService paymentService = new PaymentService();
        paymentService.setListener(this);
        JSONObject response = paymentService.createPayment(request);
        if (response.getString("status").equals("failed")) {
            // Open payment window
            SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, "error", null, response.getString("message"));
//            transactionService.updateTransactionStatus(billViewModel.getId(), TransactionStatusEnum.CANCELLED);
            System.out.println("Transaction canceled: " + billViewModel.getId());
            ((Stage) btCancel.getScene().getWindow()).close();
        }
    }

    private void handleAccept() {
        // Handle the accept action
        transactionService.updateTransactionStatus(billViewModel.getId(), TransactionStatusEnum.PAYMENT);
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

    private void handleExport() {
        // Handle the export action
        System.out.println("Exporting transaction: " + billViewModel.getId());
        // Implement export logic here
        GenerateService generateService = new GenerateService();
        generateService.generateInvoicePDF(billViewModel.getId());
    }

    private void handleChange() {
        String id = billViewModel.getId();
        paymentService.resetPayment(id);

        for (Object o : paymentMethod.getToggles()) {
            RadioButton rb = (RadioButton) o;
            rb.setDisable(false);
        }
    }

    private void displayBillInfo() {
        lbUsername.setText(accountModel.getFullName());
        lbPhone.setText(accountModel.getPhone());
        lbEmail.setText(accountModel.getEmail());

        lbBillID.setText(String.valueOf(billViewModel.getId()));
        lbDate.setText(billViewModel.getDate().format(ConfigManager.getInstance().getDateTimeFormatter()));
        lbTotal.setText(bundle.getString("total") + ": " + ConfigManager.getInstance().getNumberFormatter().format(billViewModel.getPrice()));
        lbStatus.setText(String.valueOf(bundle.getString("status." + billViewModel.getStatus().toString().toLowerCase())));

        if (paymentViewModel != null) {
            lbPaymentID.setText(String.valueOf(paymentViewModel.getPaymentID()));
            lbMethod.setText(paymentViewModel.getMethod());
            lbPaymentDate.setText(paymentViewModel.getPaymentDate().format(ConfigManager.getInstance().getDateTimeFormatter()));

        } else {
            lbPaymentID.setText("");
            lbMethod.setText("");
            lbPaymentDate.setText("");
        }

        Integer payment_method = billViewModel.getMethod();

        if (payment_method != 0) {

            lbChange.setVisible(true);
            for (Object o : paymentMethod.getToggles()) {
                RadioButton rb = (RadioButton) o;
                rb.setDisable(true);
            }

            switch (payment_method) {
                case 1 -> {
                    rbVN_Pay.setDisable(false);
                    rbVN_Pay.setSelected(true);
                }
                case 4 -> {
                    rbZalo_Pay.setDisable(false);
                    rbZalo_Pay.setSelected(true);
                }
                case 2 -> {
                    rbMoMo_Pay.setDisable(false);
                    rbMoMo_Pay.setSelected(true);
                }
            }
        }
    }

    public void setBillViewModel(BillViewModel billViewModel) {
        this.billViewModel = billViewModel;
        accountModel = getAccountModel();
        initTable();
        displayBillInfo();
    }

    private void initTable() {
        colDomainName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDomainPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDomainStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDomainYears.setCellValueFactory(new PropertyValueFactory<>("years"));

        colDomainPrice.setCellFactory(MoneyCellFactory.forTableColumn());
        colDomainYears.setCellFactory(YearCellFactory.forTableColumn());
        colDomainStatus.setCellFactory(StatusCellFactory.forTableColumn());

        updateTable();
    }

    private void updateTable() {
        ObservableList<DomainViewModel> observableList = FXCollections.observableArrayList(getDomainList());
        table.getItems().clear();
        table.setItems(observableList);
    }

    private AccountModel getAccountModel() {
        JSONObject request = new JSONObject();
        request.put("user_id", billViewModel.getUserId());

        IAccount accountServices = new AccountServices();
        JSONObject respond = accountServices.getUserInformation(request);

        String fullname = respond.getString("username");
        String phone = respond.getString("phone");
        String email = respond.getString("email");
        String pass = respond.getString("password");

        return new AccountModel(fullname, phone, email, pass);
    }

    private List<DomainViewModel> getDomainList() {
        List<DomainViewModel> domains = new ArrayList<>();

        JSONObject request = new JSONObject();
        request.put("transaction_id", billViewModel.getId());

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
                return new PaymentViewModel(ts_id, id, method, STATUS.valueOf(status), LocalDateTime.parse(date, ConfigManager.getInstance().getParser()));
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
                updateTable();
                setMethod(METHOD.REVIEW);
            } else {
                SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, "error", null, bundle.getString("notice.paymentFailed"));
                btPay.setDisable(false);
                for (Toggle rb : paymentMethod.getToggles()) {
                    ((RadioButton) rb).setDisable(false);
                }
            }
        });
    }

    private RadioButton checkPaymentMethod() {
        if (paymentMethod.getSelectedToggle() == null) {
            return null;
        }
        return (RadioButton) paymentMethod.getSelectedToggle();
    }
}