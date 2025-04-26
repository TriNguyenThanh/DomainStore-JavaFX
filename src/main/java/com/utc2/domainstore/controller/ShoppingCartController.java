package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.view.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ShoppingCartController implements Initializable {
    private ResourceBundle bundle;
    private List<DomainViewModel> data;

    @FXML
    private TableView<DomainViewModel> tbCart;
    @FXML
    private Button btRemove, btBuy;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        data = getData();
    }

    private List<DomainViewModel> getData() {
        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());


        return null;
    }
}
