package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.entity.view.TLDViewModel;
import com.utc2.domainstore.service.*;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    private ResourceBundle bundle;
    private DomainViewModel domainViewModel = new DomainViewModel();
    private static boolean isSearch = false;
    private static Integer searchCount = 0;
    private static Task<Void> currentSearchTask;

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
    private TableView<TLDViewModel> tbTLD;
    @FXML
    private TableColumn<TLDViewModel, String> colName;
    @FXML
    private TableColumn<TLDViewModel, Integer> colPrice;

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
        if (isSearch) {
            // Cancel the current search task if it's running
            if (currentSearchTask != null && currentSearchTask.isRunning()) {
                currentSearchTask.cancel();
            }
        }

        isSearch = true;
        searchCount++;
        System.out.println("Search count: " + searchCount);
        String domainName = tfSearch.getText();
        recomment.getChildren().clear();

        // Check if the domain name has a dot
        if (!domainName.isBlank() && !domainName.contains(".")) {
            domainName = domainName + ".com";
        }

        // Create a new search task
        String finalDomainName = domainName;
        currentSearchTask = new Task<>() {
            @Override
            protected Void call() {
                searchWithDomainName(finalDomainName.toLowerCase());
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                isSearch = false; // Reset the flag after the search is complete
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                isSearch = false; // Reset the flag if the task is canceled
            }

            @Override
            protected void failed() {
                super.failed();
                isSearch = false; // Reset the flag if the task fails
            }
        };

        // Run the task in a background thread
        new Thread(currentSearchTask).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.tfSearch.setOnAction(event -> {
            if (!isSearch)
                handleSearch();
        });
        initTable();
    }

    private void initTable() {
        // init column
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(MoneyCellFactory.forTableColumn());

        // set data
        tbTLD.setItems(FXCollections.observableArrayList(getAllTLD()));
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
            recomment.getChildren().clear();
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
            SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("error"), null, bundle.getString("notice.domainNotSuported"));
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

    // get all TLD
    public List<TLDViewModel> getAllTLD() {
        List<TLDViewModel> tldList = new ArrayList<>();
        ITopLevelDomain topLevelDomainService = new TopLevelDomainServices();
        JSONObject respond = topLevelDomainService.getAllTLD();
        if (respond.has("tld")) {
            JSONArray tlds = respond.getJSONArray("tld");
            for (Object o : tlds) {
                JSONObject tld = (JSONObject) o;
                String name = tld.getString("TLD_text");
                Integer price = tld.getInt("price");
                Integer id = tld.getInt("id");

                tldList.add(new TLDViewModel(id, name, price));
            }
        }
        return tldList;
    }
}
