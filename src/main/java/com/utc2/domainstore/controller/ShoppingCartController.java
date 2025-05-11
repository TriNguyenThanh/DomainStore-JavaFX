package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.CartServices;
import com.utc2.domainstore.service.ICart;
import com.utc2.domainstore.service.TransactionService;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.utils.SpinnerCellFactory;
import com.utc2.domainstore.utils.StatusCellFactory;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
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
    private ICart cartService = new CartServices();

    // FXML components
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

    // initialize the table
    private void initTable() {
        colDomain.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("years"));

        colPrice.setCellFactory(MoneyCellFactory.forTableColumn());
        colStatus.setCellFactory(StatusCellFactory.forTableColumn());
        colYear.setEditable(true);
        colYear.setCellFactory(SpinnerCellFactory.forTableColumn(
                1, 10, 1,
                DomainViewModel::getYears, // Getter
                DomainViewModel::setYears,  // Setter
                this::updateTotal
        ));

        tbCart.setItems(FXCollections.observableArrayList(getData()));
        tbCart.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tbCart.setOnMouseClicked(event -> updateTotal());
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
            domainJson.put("price", domain.getPrice());
            domainJson.put("years", domain.getYears());
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
        // check if there is no item selected
        if (selectedItems.isEmpty()) {
            SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("notice"), null, bundle.getString("error.noSelect"));
            return;
        }
        // update the cart
        updateCart(selectedItems);

        // create transaction
        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());
        JSONArray domainArray = new JSONArray();
        for (DomainViewModel domain : selectedItems) {
            JSONObject domainJson = new JSONObject();
            domainJson.put("name", domain.getName());
            domainJson.put("price", domain.getPrice());
            domainJson.put("years", domain.getYears());
            domainArray.put(domainJson);
        }
        request.put("domains", domainArray);
        TransactionService transactionService = new TransactionService();
        JSONObject respond = transactionService.createTransaction(request);
        if (respond.getString("status").equals("success")) {
            // open transaction info page
            SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("notice"), bundle.getString("notice.createTransactionSuccess"), bundle.getString("notice.createTransactionSuccess") + ": " + respond.getString("transactionId"));

            String currentFXMLPath = "/fxml/transaction.fxml";
            MainController.getInstance().setCurrentFxmlPath(currentFXMLPath);
            MainController.getInstance().load(currentFXMLPath, true);
        } else {
            // show error message
            SceneManager.getInstance().showDialog(Alert.AlertType.WARNING, bundle.getString("error"), bundle.getString("notice"), bundle.getString("notice.createTransactionFailed"));
        }
        remove();
    }

    private void updateTotal() {
        ObservableList<DomainViewModel> list = tbCart.getSelectionModel().getSelectedItems();
        int sum = 0;
        for (DomainViewModel domain : list) {
            sum += domain.getPrice() * domain.getYears();
        }
        if (sum != 0)
            lbTotal.setText(ConfigManager.getInstance().getNumberFormatter().format(sum));
    }

    private void updateCart(ObservableList<DomainViewModel> selectedList) {
        JSONObject request = new JSONObject();
        request.put("cus_id", UserSession.getInstance().getUserId());
        JSONArray domainArray = new JSONArray();
        for (DomainViewModel domain : selectedList) {
            JSONObject domainJson = new JSONObject();
            domainJson.put("name", domain.getName());
            domainJson.put("price", domain.getPrice());
            domainJson.put("years", domain.getYears());
            domainJson.put("status", domain.getStatus().toString().toLowerCase());
            domainArray.put(domainJson);
        }
        request.put("domain", domainArray);
        JSONObject response = cartService.updateCart(request);
        if (response.getString("status").equals("success")) {
            System.out.println("Updated successfully");
        } else {
            SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, bundle.getString("error"), bundle.getString("notice"), bundle.getString("notice.updateCartFailed"));
        }
    }
}
