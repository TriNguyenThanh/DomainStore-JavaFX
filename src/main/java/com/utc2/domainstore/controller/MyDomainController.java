package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.IDomain;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.view.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyDomainController implements Initializable {
    private ResourceBundle bundle;

    @FXML
    private TableView<DomainViewModel> tbDomain;
    @FXML
    private TableColumn<DomainViewModel, String> colDomain;
    @FXML
    private TableColumn<DomainViewModel, Integer> colPrice, colYear;
    @FXML
    private TableColumn<DomainViewModel, LocalDate> colDate;
    @FXML
    private Button btUpdate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        btUpdate.setVisible(false);
        initTable();
    }

    private void initTable() {
        // Set up the table columns
        colDomain.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("years"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        colDate.setCellFactory(LocalDateCellFactory.forTableColumn());
        colPrice.setCellFactory(MoneyCellFactory.forTableColumn());

        tbDomain.setItems(FXCollections.observableArrayList(getData()));
    }

    // Method to get data from the server
    private List<DomainViewModel> getData() {
        List<DomainViewModel> list = new ArrayList<>();

        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());
        IDomain domainService = new DomainServices();
        JSONObject response = domainService.searchSoldDomainByCusId(request);
        JSONArray domainArray = response.getJSONArray("domain");
        if (domainArray != null) {
            for (int i = 0; i < domainArray.length(); i++) {
                JSONObject domainObject = domainArray.getJSONObject(i);
                String name = domainObject.optString("name", "");
                String status = domainObject.optString("status", "");
                Integer price = domainObject.optInt("price");
                Integer years = domainObject.optInt("year");
                LocalDate date = LocalDate.parse(domainObject.opt("active_date").toString());

                DomainViewModel domainViewModel = new DomainViewModel(name, STATUS.valueOf(status.toUpperCase()), price, years, date);
                list.add(domainViewModel);
            }
        }
        return list;
    }
}
