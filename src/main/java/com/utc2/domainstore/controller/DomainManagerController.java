package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.IDomain;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import com.utc2.domainstore.utils.MoneyCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DomainManagerController implements Initializable {
    private ResourceBundle bundle;
    private List<DomainViewModel> data;

    @FXML
    private TableView<DomainViewModel> tbDomain;
    @FXML
    private TableColumn<DomainViewModel, String> colDomain;
    @FXML
    private TableColumn<DomainViewModel, STATUS> colStatus;
    @FXML
    private TableColumn<DomainViewModel, Integer> colPrice;
    @FXML
    private TableColumn<DomainViewModel, Integer> colYear;
    @FXML
    private TableColumn<DomainViewModel, LocalDate> colDate;
    @FXML
    private TableColumn<DomainViewModel, String> colOwner;
    @FXML
    private Button btAdd, btRemove;
    @FXML
    private TextField tfSearch;
    @FXML
    private ComboBox<String> cbStatus;

    @FXML
    private void onHandleButton(ActionEvent event) {
        // Handle button click event
        if (event.getSource() == btAdd) {
            // Perform action based on the button clicked
            handleAddButton();
        } else if (event.getSource() == btRemove) {
            // Perform action based on the button clicked
            handleRemoveButton();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        data = getData();

        cbStatus.getItems().addAll(List.of("", STATUS.AVAILABLE.toString(), STATUS.SOLD.toString()));
        initTable();
    }

    private void initTable() {
        // Set up the table columns
        colDomain.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("years"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));

        colPrice.setCellFactory(MoneyCellFactory.forTableColumn());
        colDate.setCellFactory(LocalDateCellFactory.forTableColumn());

        updateTable();
    }

    private void updateTable() {
        ObservableList<DomainViewModel> observableList = FXCollections.observableArrayList(data);
        FilteredList<DomainViewModel> filteredData = new FilteredList<>(observableList, p -> true);

        // Add a listener to the filter property of the filtered list
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> updateFilteredData(filteredData));
        cbStatus.valueProperty().addListener((observable, oldValue, newValue) -> updateFilteredData(filteredData));
//        tbDomain.getItems().clear();
        tbDomain.setItems(filteredData);
    }

    // Handle remove button actions
    private void handleRemoveButton() {

    }

    // Handle add button actions
    private void handleAddButton() {

    }

    // Get data from the server
    private List<DomainViewModel> getData() {
        List<DomainViewModel> list = new ArrayList<>();
        IDomain domainService = new DomainServices();
        JSONObject response = domainService.getAllDomains();
        if (response != null) {
            for (int i = 0; i < response.getJSONArray("domain").length(); i++) {
                JSONObject domain = response.getJSONArray("domain").getJSONObject(i);
                String name = domain.getString("name");
                STATUS status = STATUS.valueOf(domain.getString("status").toUpperCase());
                Integer price = domain.getInt("price");
                Integer years = null;
                Integer ownerId = null;
                String ownerName = null;
                String activeDate = domain.get("active_date").toString();
                LocalDate date = null;

                if (!activeDate.equals("0")) {
                    date = LocalDate.parse(activeDate);
                    years = domain.getInt("year");
                    ownerId = domain.getInt("owner_id");
                    ownerName = domain.getString("user_name");
                }

                DomainViewModel domainViewModel = new DomainViewModel(name, status, price, years, date, ownerId, ownerName);
                list.add(domainViewModel);
            }
        }
        return list;
    }

    // Update the filtered list based on the search text and selected status
    private void updateFilteredData(FilteredList<DomainViewModel> filteredData) {
        filteredData.setPredicate(domain -> {
            String searchText = tfSearch.getText().toLowerCase();
            String selectedStatus = cbStatus.getValue();

            // 1. Check if the domain name contains the search text
            boolean matchesSearch = domain.getName().toLowerCase().contains(searchText);

            // 2. Check if the domain status matches the selected status
            boolean matchesStatus = selectedStatus == null || selectedStatus.isEmpty() || domain.getStatus().toString().equalsIgnoreCase(selectedStatus);

            // 3. Return true if both conditions are met
            return matchesSearch && matchesStatus;
        });
    }
}
