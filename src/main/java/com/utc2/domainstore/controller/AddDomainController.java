package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.TLDViewModel;
import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.IDomain;
import com.utc2.domainstore.service.ITopLevelDomain;
import com.utc2.domainstore.service.TopLevelDomainServices;
import com.utc2.domainstore.view.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddDomainController implements Initializable {
    private ResourceBundle bundle;
    private String domainName;
    private final IDomain domainService = new DomainServices();
    private ITopLevelDomain tldService;
    private Runnable listener;
    private static AddDomainController instance;

    public AddDomainController() {
        if (instance == null) {
            instance = this;
        }
    }

    public static AddDomainController getInstance() {
        return instance;
    }

    // FXML components
    @FXML
    private TextField tfName;
    @FXML
    private ComboBox<String> cbTLD;
    @FXML
    private Label lbDomain;
    @FXML
    private Button btCancel, btSave;

    @FXML
    private void onHandleButton(ActionEvent event) {
        if (event.getSource() == btSave) {
            // Handle save button click
            handleSave();
        } else if (event.getSource() == btCancel) {
            // Handle cancel button click
            handleCancel();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        tldService = new TopLevelDomainServices();

        cbTLD.getItems().addAll(getTLDs().stream().map(TLDViewModel::getName).toList());
        cbTLD.getSelectionModel().selectFirst();

        tfName.textProperty().addListener((observable, oldValue, newValue) -> {
            String tld = cbTLD.getSelectionModel().getSelectedItem();
            if (tld != null) {

                // Check if the new value is valid
                if (newValue.matches("[a-zA-Z0-9]+")) {
                    // Update the domain label with the new value
                    domainName = newValue + tld;
                    lbDomain.setText(domainName);
                } else if (newValue.isEmpty()) {
                    domainName = "";
                    lbDomain.setText(domainName);
                } else {
                    // Show error message if the new value is invalid
                    lbDomain.setText(bundle.getString("notice.domainNotAvailable"));
                }
            }
        });

        tfName.setOnAction(actionEvent -> handleSave());

        cbTLD.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String tld = newValue;
            if (tld != null) {
                // Update the domain label with the new value
                domainName = tfName.getText() + tld;
                lbDomain.setText(domainName);
            }
        });
    }

    private void handleCancel() {
        ((Stage) btCancel.getParent().getScene().getWindow()).close();
    }

    private void handleSave() {
        // Validate domain name
        if (domainName == null || domainName.isEmpty() || !domainName.contains(".")) {
            SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, bundle.getString("error"), null, bundle.getString("notice.domainNotAvailable"));
            return;
        }

        JSONObject request = new JSONObject();
        request.put("name", domainName);

        JSONObject response = domainService.insertNewDomain(request);
        if (response != null) {
            String message = response.getString("message");
            if (response.getString("status").equals("success")) {
                // Handle success response
                System.out.println("Domain added successfully: " + message);
                if (listener != null) {
                    Platform.runLater(() -> {
                        listener.run();
                    });
                }
                ((Stage) btSave.getParent().getScene().getWindow()).close();
            } else {
                // Handle error response
                SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, bundle.getString("error"), null, bundle.getString("notice.domainNotAvailable"));
            }
        } else {
            // Handle null response
            SceneManager.getInstance().showDialog(Alert.AlertType.ERROR, bundle.getString("error"), null, bundle.getString("notice.serverError"));
        }

    }

    public void setListener(Runnable func) {
        listener = func;
    }

    // Get TLDs from the server
    private List<TLDViewModel> getTLDs() {
        List<TLDViewModel> list = new ArrayList<>();
        JSONObject response = tldService.getAllTLD();
        if (response != null) {
            for (int i = 0; i < response.getJSONArray("tld").length(); i++) {
                JSONObject tld = response.getJSONArray("tld").getJSONObject(i);
                Integer id = tld.getInt("id");
                String name = tld.getString("TLD_text");
                Integer price = tld.getInt("price");
                TLDViewModel tldViewModel = new TLDViewModel(id, name, price);
                list.add(tldViewModel);
            }
        }
        return list;
    }
}
