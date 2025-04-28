package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.CartServices;
import com.utc2.domainstore.service.ICart;
import com.utc2.domainstore.service.TransactionService;
import com.utc2.domainstore.view.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ShoppingCartController implements Initializable {
    private ResourceBundle bundle;
    private List<DomainViewModel> data;

    @FXML
    private TableView<DomainViewModel> tbCart;
    @FXML
    private TableColumn<DomainViewModel, String> colDomain;
    @FXML
    private TableColumn<DomainViewModel, STATUS> colStatus;
    @FXML
    private TableColumn<DomainViewModel, Integer> colPrice, colYear;
    @FXML
    private Button btRemove, btBuy;
    @FXML
    private Label lbTotal;

    @FXML
    private void handleButtonOnAction(ActionEvent event) throws IOException {
        if (event.getSource() == btRemove) {
            remove();
        } else if (event.getSource() == btBuy) {
            buy();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        data = getData();
        initTable();
    }

    private void initTable() {
        colDomain.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("years"));

        tbCart.setItems(FXCollections.observableArrayList(getData()));
        tbCart.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tbCart.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<DomainViewModel> list = tbCart.getSelectionModel().getSelectedItems();
            int sum = 0;
            for (DomainViewModel domain : list) {
                sum += domain.getPrice() * domain.getYears();
            }
            lbTotal.setText(String.format(": %,3d VND", sum));
        });
    }

    private List<DomainViewModel> getData() {
        List<DomainViewModel> data = new ArrayList<>();
        JSONObject request = new JSONObject();
        request.put("cus_id", UserSession.getInstance().getUserId());

        ICart cartService = new CartServices();
        JSONObject response = cartService.getShoppingCart(request);

        JSONArray domainArray = response.getJSONArray("domain");
        for (int i = 0; i < domainArray.length(); i++) {
            JSONObject domainJson = domainArray.getJSONObject(i);
            DomainViewModel domainViewModel = new DomainViewModel();
            domainViewModel.setName(domainJson.getString("name"));
            domainViewModel.setStatus(STATUS.valueOf(domainJson.getString("status").toUpperCase()));
            domainViewModel.setPrice(domainJson.getInt("price"));
            domainViewModel.setYears(domainJson.getInt("year"));
            data.add(domainViewModel);
        }
        return data;
    }

    private void remove() {
        ObservableList<DomainViewModel> selectedItems = tbCart.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            return;
        }
        data.removeAll(selectedItems);
        JSONObject request = new JSONObject();
        request.put("cus_id", UserSession.getInstance().getUserId());
        JSONArray domainArray = new JSONArray();
        for (DomainViewModel domain : selectedItems) {
            JSONObject domainJson = new JSONObject();
            domainJson.put("name", domain.getName());
            domainArray.put(domainJson);
        }
        request.put("domain", domainArray);
        JSONObject response = new CartServices().removeFromCart(request);
        if (response.getString("status").equals("success")) {
            System.out.println("Removed successfully");
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString("error"));
            alert.setHeaderText(null);
            alert.setContentText(bundle.getString("remove_failed"));
            alert.showAndWait();
        }

        tbCart.setItems(FXCollections.observableArrayList(data));
        lbTotal.setText(String.format(": %,3d VND", 0));
    }

    private void buy() throws IOException {
        ObservableList<DomainViewModel> selectedItems = tbCart.getSelectionModel().getSelectedItems();
        remove();
        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());
        JSONArray domainArray = new JSONArray();
        for (DomainViewModel domain : selectedItems) {
            JSONObject domainJson = new JSONObject();
            domainJson.put("name", domain.getName());
            domainArray.put(domainJson);
        }
        request.put("domains", domainArray);
        TransactionService transactionService = new TransactionService();
        JSONObject respond = transactionService.createTransaction(request);
        if (respond.getString("status").equals("success")) {
            // open transaction info page
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString("success"));
            alert.setHeaderText(null);
            alert.setContentText(respond.getString("transactionId"));
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString("error"));
            alert.setHeaderText(null);
            alert.setContentText("Mua khong thanh cong");
            alert.showAndWait();
        }
    }
}
