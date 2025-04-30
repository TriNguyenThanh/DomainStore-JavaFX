package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.IDomain;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private TableColumn<DomainViewModel, Integer> colOwner;

    @FXML
    private Button btAdd, btRemove, btEdit;

    @FXML
    private void onHandleButton(ActionEvent event) {
        // Handle button click event
        if (event.getSource() == btAdd) {
            // Perform action based on the button clicked
        } else if (event.getSource() == btRemove) {
            // Perform action based on the button clicked
        } else if (event.getSource() == btEdit) {
            // Perform action based on the button clicked
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
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerId"));

        colDate.setCellFactory(LocalDateCellFactory.forTableColumn());

        tbDomain.getItems().clear();
        tbDomain.getItems().addAll(data);
    }

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

                String activeDate = domain.get("active_date").toString();
                LocalDate date = null;
                if (!activeDate.equals("0")) {
                    date = LocalDate.parse(activeDate);
                    years = domain.getInt("year");
                    ownerId = domain.getInt("owner_id");
                }

                DomainViewModel domainViewModel = new DomainViewModel(name, status, price, years, date, ownerId);
                list.add(domainViewModel);
            }
        }
        return list;
    }
}
