package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.IDomain;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.utils.StatusCellFactory;
import com.utc2.domainstore.utils.YearCellFactory;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DomainManagerController implements Initializable {
    private ResourceBundle bundle;
    private List<DomainViewModel> data;
    private final IDomain domainService = new DomainServices();

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

        btRemove.setVisible(false);
        cbStatus.getItems().addAll(List.of("", bundle.getString("status.available"), bundle.getString("status.sold")));
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
        colYear.setCellFactory(YearCellFactory.forTableColumn());
        colStatus.setCellFactory(StatusCellFactory.forTableColumn());

        tbDomain.setOnMouseClicked(event -> {
            DomainViewModel selectedDomain = tbDomain.getSelectionModel().getSelectedItem();
            if (selectedDomain != null) {
                // Perform action with the selected domain
                btRemove.setDisable(selectedDomain.getStatus() == STATUS.SOLD);
            }
        });

        updateTable();
    }

    // Update the table with data
    private void updateTable() {
        ObservableList<DomainViewModel> observableList = FXCollections.observableArrayList(getData());
        FilteredList<DomainViewModel> filteredData = new FilteredList<>(observableList, p -> true);

        // Add a listener to the filter property of the filtered list
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> updateFilteredData(filteredData));
        cbStatus.valueProperty().addListener((observable, oldValue, newValue) -> updateFilteredData(filteredData));
//        tbDomain.getItems().clear();
        tbDomain.setItems(filteredData);
    }

    // Handle remove button actions
    private void handleRemoveButton() {
        // get selected domain
        DomainViewModel selectedDomain = tbDomain.getSelectionModel().getSelectedItem();
        if (selectedDomain == null) {
            // Show an error message if no domain is selected
            SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, bundle.getString("error"), null, bundle.getString("error.noSelect"));
            return;
        }
        // create request
        JSONObject request = new JSONObject();
        request.put("name", selectedDomain.getName());

        // send request to server
        JSONObject response = domainService.deleteAvailableDomain(request);
        if (response != null) {
            // Show success message
            SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("notice"), null, bundle.getString("notice.deleteDomainSuccess"));
            // remove domain from list
            data.remove(selectedDomain);
            updateTable();
        } else {
            // Show error message
            SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, bundle.getString("error"), null, bundle.getString("notice.deleteDomainFailed"));
        }
    }

    // Handle add button actions
    private void handleAddButton() {
        try {
            // initialize the AddDomainUI
            Stage stage = new Stage();
            stage.initOwner(SceneManager.getInstance().getStage());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(bundle.getString("add") + " " + bundle.getString("domainName"));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.getIcons().add(SceneManager.getInstance().getIcon("/image/logoUTC2.png", 16, 16));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addDomain.fxml"), ConfigManager.getInstance().getLanguageBundle());
            stage.setScene(new Scene(loader.load()));
            AddDomainController.getInstance().setListener(() -> {
                // Reload the data after adding a new domain
                data = getData();
                updateTable();
            });
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Get data from the server
    private List<DomainViewModel> getData() {
        List<DomainViewModel> list = new ArrayList<>();
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
            boolean matchesStatus = selectedStatus == null || selectedStatus.isEmpty() || bundle.getString("status." + domain.getStatus().toString().toLowerCase()).equalsIgnoreCase(selectedStatus);

            // 3. Return true if both conditions are met
            return matchesSearch && matchesStatus;
        });
    }
}
