package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.BillViewModel;
import com.utc2.domainstore.entity.view.METHOD;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.ITransactionService;
import com.utc2.domainstore.service.TransactionService;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class TransactionController implements Initializable {
    private ResourceBundle bundle;

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
    private Button getSelectedButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        getSelectedButton.setVisible(false);
        initTable();
    }

    private void initTable() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        colPrice.setCellFactory(MoneyCellFactory.forTableColumn());
        colDate.setCellFactory(LocalDateCellFactory.forTableColumn());

        tableView.setOnMouseClicked(event -> {
            BillViewModel selectedBill = tableView.getSelectionModel().getSelectedItem();
            if (selectedBill == null) {
                getSelectedButton.setVisible(false);
                return;
            }
            getSelectedButton.setVisible(true);
            // thay đổi text của nút theo trạng thái của hóa đơn
            if (selectedBill.getStatus() == STATUS.COMPLETED || selectedBill.getStatus() == STATUS.CANCELLED) {
                getSelectedButton.setText(bundle.getString("review"));
            } else if (selectedBill.getStatus() == STATUS.PENDINGCONFIRM) {
                getSelectedButton.setText(bundle.getString("confirm"));
            } else if (selectedBill.getStatus() == STATUS.PENDINGPAYMENT) {
                getSelectedButton.setText(bundle.getString("pay"));
            }

            // nếu nhấn đúp chuột vào dòng trong bảng
            if (event.getClickCount() == 2) {

                BillInfo(selectedBill);
            }
        });

        getSelectedButton.setOnAction(event -> {
            BillViewModel selectedBill = tableView.getSelectionModel().getSelectedItem();
            if (selectedBill != null) {
                // mở một cửa sổ mới với thông tin chi tiết của hóa đơn
                BillInfo(selectedBill);
            }
//            else {
//                System.out.println("No row selected");
//            }
        });

        ObservableList<BillViewModel> billObservableList = FXCollections.observableArrayList(getData());
        tableView.setItems(billObservableList);
    }

    private void BillInfo(BillViewModel selectedBill) {
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
        if (selectedBill.getStatus() == STATUS.COMPLETED || selectedBill.getStatus() == STATUS.CANCELLED) {
            transactionInfoController.setMethod(METHOD.REVIEW);
        } else if (selectedBill.getStatus() == STATUS.PENDINGPAYMENT || selectedBill.getStatus() == STATUS.PENDINGCONFIRM) {
            transactionInfoController.setMethod(METHOD.PAY);
        }

        Scene billInfoScene = new Scene(root);
        billInfoStage.setScene(billInfoScene);

        billInfoStage.initOwner(SceneManager.getInstance().getStage());
        billInfoStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
        billInfoStage.showAndWait();

        // Sau khi đóng cửa sổ, cập nhật lại bảng
        initTable();
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
            LocalDate date = LocalDate.parse(jsonObject.optString("date"));
            STATUS status = STATUS.valueOf(jsonObject.get("status").toString());
            Integer price = jsonObject.getInt("total_price");
            Integer userId = jsonObject.getInt("user_id");
            
            bills.add(new BillViewModel(id, date, status, price, userId));
        }

        return bills;
    }
}