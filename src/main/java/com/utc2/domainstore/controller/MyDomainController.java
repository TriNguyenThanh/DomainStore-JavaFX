package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.IDomain;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import com.utc2.domainstore.view.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private List<DomainViewModel> data;

    @FXML
    private TableView<DomainViewModel> tbDomain;
    @FXML
    private TableColumn<DomainViewModel, String> colDomain;
    @FXML
    private TableColumn<DomainViewModel, String> colStatus;
    @FXML
    private TableColumn<DomainViewModel, Integer> colPrice, colYear;
    @FXML
    private TableColumn<DomainViewModel, LocalDate> colDate;


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

        colDate.setCellFactory(LocalDateCellFactory.forTableColumn());

        tbDomain.getItems().clear();
        tbDomain.getItems().addAll(data);
    }

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
                int price = domainObject.optInt("price");
                int years = domainObject.optInt("year");
                LocalDate date = LocalDate.parse(domainObject.optString("active_date", ""));

                DomainViewModel domainViewModel = new DomainViewModel(name, STATUS.valueOf(status.toUpperCase()), price, years, date);
                list.add(domainViewModel);
            }
        }
        return list;
    }
}
