package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.CartServices;
import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.ICart;
import com.utc2.domainstore.service.IDomain;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    private ResourceBundle bundle;
    private DomainViewModel domainViewModel = new DomainViewModel();

    // FXML components
    @FXML
    private TextField tfSearch;
    @FXML
    private Button btSearch, btAdd;
    @FXML
    private Label lbDomain, lbStatus, lbPrice;
    @FXML
    private HBox recomment;

    @FXML
    private void handleButton(ActionEvent e) {
        if (e.getSource() == btSearch) {
            handleSearch();
        } else if (e.getSource() == btAdd) {
            handleAdd();
        }
    }

    // Method to handle the add button click
    private void handleAdd() {
        if (domainViewModel.getStatus() == STATUS.SOLD) {
            SceneManager.getInstance().showDialog(Alert.AlertType.WARNING, bundle.getString("warning"), null, bundle.getString("notice.chooseAnotherDomain"));
            return;
        }
        // create a JSON object to send to the server
        JSONObject request = new JSONObject();
        ICart cartService = new CartServices();
        request.put("cus_id", UserSession.getInstance().getUserId());
        JSONArray domainArray = new JSONArray();
        JSONObject domainJson = new JSONObject();
        domainJson.put("name", domainViewModel.getName());
        domainJson.put("status", domainViewModel.getStatus().toString().toLowerCase());
        domainJson.put("price", domainViewModel.getPrice());
        domainJson.put("years", 1);
        domainArray.put(domainJson);
        request.put("domain", domainArray);

        // parse the response
        JSONObject respond = cartService.addToCart(request);
        String status = respond.getString("status");
        String message = respond.getString("message");

        // show the alert
        String title = bundle.getString("addToCart");
        String content;
        if (status.equals("success")) {
            content = bundle.getString("notice.addToCartSuccess");
            SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, title, null, content);
            // go to shopping cart
            MainController.getInstance().load("/fxml/shoppingCart.fxml");
        } else {
            content = bundle.getString("notice.addToCartFailed") + ": " + message;
            SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, title, null, content);
        }
    }

    // Method to handle the search button click
    private void handleSearch() {
        String domainName = tfSearch.getText();
        recomment.getChildren().clear();
        searchWithDomainName(domainName.toLowerCase());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.tfSearch.setOnAction(event -> {
            searchWithDomainName(tfSearch.getText().toLowerCase());
        });
        searchWithDomainName("");
    }

    // Method to search for a domain name
    private void searchWithDomainName(String domainName) {
        // Call the search method from DomainServices
        IDomain domainSearch = new DomainServices();

        JSONObject request = new JSONObject();
        request.put("name", domainName);

        JSONObject respond = domainSearch.search(request);

        // Update UI
        if (respond.has("domain")) {
            // parse the response
            JSONArray results = respond.getJSONArray("domain");
            for (Object o : results) {
                JSONObject domain = (JSONObject) o;
                String name = domain.getString("name");
                String status = domain.getString("status");
                String price = String.valueOf(domain.getInt("price"));

                // Update UI
                lbDomain.setText(name);
                lbStatus.setText(bundle.getString(String.valueOf(status).toLowerCase()));
                lbPrice.setText(ConfigManager.getInstance().getNumberFormatter().format(Integer.parseInt(price)));

                domainViewModel.setName(name);
                domainViewModel.setStatus(STATUS.valueOf(status.toUpperCase()));
                domainViewModel.setPrice(Integer.parseInt(price));
                domainViewModel.setYears(1);

                lbStatus.setStyle("-fx-text-fill: #00FF00;");
                lbDomain.setStyle("-fx-text-fill: #00FF00;");
                lbPrice.setStyle("-fx-text-fill: #00FF00;");

                // set event for label
                Label label = new Label(name);
                label.setOnMouseClicked(event -> {
                    searchWithDomainName(name);
                });

                label.setOnMouseEntered(event -> {
                    label.setStyle("-fx-text-fill: #00FF00;");
                });

                label.setOnMouseExited(event -> {
                    label.setStyle("-fx-text-fill: #000000;");
                });

                recomment.getChildren().add(label);
            }
        } else if (respond.getString("status").equals("failed")) {
            lbDomain.setText("");
            lbStatus.setText("");
            lbPrice.setText("");
            lbDomain.setStyle("-fx-text-fill: #FF0000;");
            lbStatus.setStyle("-fx-text-fill: #FF0000;");
            lbPrice.setStyle("-fx-text-fill: #FF0000;");

            // Show the error message
            SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("error"), null, bundle.getString("notice.domainNotSuported") + ": '" + domainName.substring(domainName.lastIndexOf('.')) + "'");
        } else {
            // parse the response
            String name = respond.getString("name");
            String status = respond.getString("status");
            String price = String.valueOf(respond.getInt("price"));

            // Update UI
            lbDomain.setText(name);
            lbStatus.setText(bundle.getString(String.valueOf(status).toLowerCase()));
            lbPrice.setText(ConfigManager.getInstance().getNumberFormatter().format(Integer.parseInt(price)));

            if (STATUS.valueOf(status.toUpperCase()) == STATUS.AVAILABLE) {
                lbStatus.setStyle("-fx-text-fill: #00FF00;");
                lbDomain.setStyle("-fx-text-fill: #00FF00;");
                lbPrice.setStyle("-fx-text-fill: #00FF00;");
            } else if (STATUS.valueOf(status.toUpperCase()) == STATUS.SOLD) {
                lbDomain.setStyle("-fx-text-fill: #FF0000;");
                lbStatus.setStyle("-fx-text-fill: #FF0000;");
                lbPrice.setStyle("-fx-text-fill: #FF0000;");
            }

            // set domainViewModel
            domainViewModel.setName(name);
            domainViewModel.setStatus(STATUS.valueOf(status.toUpperCase()));
            domainViewModel.setPrice(Integer.parseInt(price));
            domainViewModel.setYears(1);
        }
    }
}
