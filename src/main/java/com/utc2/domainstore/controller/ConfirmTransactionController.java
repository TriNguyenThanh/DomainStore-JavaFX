package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.BillViewModel;
import com.utc2.domainstore.entity.view.METHOD;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.ITransactionService;
import com.utc2.domainstore.service.TransactionService;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.utils.StatusCellFactory;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConfirmTransactionController implements Initializable {
    private ResourceBundle bundle;

    // FXML components
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
    private Button btInfo;

    @FXML
    private void onHandleButton(ActionEvent e) {
        if (e.getSource() == btInfo) {
            BillViewModel selectedBill = tableView.getSelectionModel().getSelectedItem();
            if (selectedBill != null) {
                // open the bill detail view
                openBillInfo(selectedBill);
            } else {
                // show an error message
                SceneManager.getInstance().showDialog(Alert.AlertType.WARNING,
                        bundle.getString("warning"), null, bundle.getString("error.noSelect"));
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        initTable();
    }

    // Method to initialize the table
    private void initTable() {
        // Set up the table columns
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        colPrice.setCellFactory(MoneyCellFactory.forTableColumn());
        colDate.setCellFactory(LocalDateCellFactory.forTableColumn());
        colStatus.setCellFactory(StatusCellFactory.forTableColumn());

        tableView.setPlaceholder(new Label(bundle.getString("placeHolder.tableEmpty")));

        tableView.setOnMouseClicked(event -> {
            BillViewModel selectedBill = tableView.getSelectionModel().getSelectedItem();
            if (selectedBill == null) {
                return;
            }
            // nếu nhấn đúp chuột vào dòng trong bảng
            if (event.getClickCount() == 2) {
                openBillInfo(selectedBill);
            }
        });

        updateTable();
    }

    // Method to update the table with data
    private void updateTable() {
        ObservableList<BillViewModel> billObservableList = FXCollections.observableArrayList(getData());
        FilteredList<BillViewModel> filteredList = new FilteredList<>(billObservableList, b -> true);

        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(billViewModel -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return billViewModel.getId().toLowerCase().contains(lowerCaseFilter);
            });
        });

        tableView.setItems(filteredList);
    }

    // get data from server
    private List<BillViewModel> getData() {
        List<BillViewModel> bills = new ArrayList<>();

        ITransactionService transactionService = new TransactionService();
        JSONObject respond = transactionService.getAllTransaction();
        JSONArray list = respond.getJSONArray("transactions");

        // parse the response
        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            STATUS status = STATUS.valueOf(jsonObject.get("status").toString());
            if (status != STATUS.PENDINGCONFIRM) {
                continue;
            }
            String id = jsonObject.getString("id");
            LocalDate date = LocalDate.parse(jsonObject.optString("date"));
            Integer price = jsonObject.getInt("total_price");
            Integer userId = jsonObject.getInt("user_id");

            bills.add(new BillViewModel(id, date, status, price, userId));
        }

        return bills;
    }

    // Method to handle opening the bill information
    private void openBillInfo(BillViewModel selectedBill) {
        Stage billInfoStage = new Stage();
        billInfoStage.setTitle(bundle.getString("information"));
        billInfoStage.setResizable(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transactionInfo.fxml"), ConfigManager.getInstance().getLanguageBundle());

        AnchorPane root = null;
        try {
            root = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TransactionInfoController transactionInfoController = loader.getController();
        transactionInfoController.setBillViewModel(selectedBill);
        transactionInfoController.setMethod(METHOD.CONFIRM);

        Scene billInfoScene = new Scene(root);
        billInfoStage.setScene(billInfoScene);

        billInfoStage.initOwner(SceneManager.getInstance().getStage());
        billInfoStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
        billInfoStage.showAndWait();

        initTable();
    }
}