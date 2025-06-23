package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.TLDViewModel;
import com.utc2.domainstore.service.ITopLevelDomain;
import com.utc2.domainstore.service.TopLevelDomainServices;
import com.utc2.domainstore.utils.CheckingUtils;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.view.SceneManager;
import javafx.collections.transformation.FilteredList;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class TLDManagerController implements Initializable {
    private ResourceBundle bundle;
    private ITopLevelDomain tldServices = new TopLevelDomainServices();
    private TLDViewModel selectedTLD = null;

    // initialize UI components
    @FXML
    private TableView<TLDViewModel> table;
    @FXML
    private TableColumn<TLDViewModel, Integer> colID;
    @FXML
    private TableColumn<TLDViewModel, Integer> colPrice;
    @FXML
    private TableColumn<TLDViewModel, String> colText;
    @FXML
    private TextField tfSearch, tfTLD, tfPrice;
    @FXML
    private Button btAdd, btEdit, btSave, btCancel;
    @FXML
    private HBox ActionButtons;

    @FXML
    private void onHandleButton(ActionEvent e) {
        if (e.getSource() == btAdd) {
            // handle add action
            handleAddButton();
        } else if (e.getSource() == btEdit) {
            // handle edit action
            handleEditButton();
        } else if (e.getSource() == btSave) {
            // handle save action
            handleSaveButton();
        } else if (e.getSource() == btCancel) {
            // handle cancel action
            handleCancelButton();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        //
        ActionButtons.setVisible(false);
        // set up button
        setupEditor();
        // set up table
        initTable();
    }

    // set up button and text field
    private void setupEditor() {
        btAdd.setVisible(false);
        btSave.setVisible(false);
        btCancel.setVisible(false);

        tfTLD.setEditable(false);
        tfPrice.setEditable(false);

        // add listener to table
        table.setOnMouseClicked(event -> {
            selectedTLD = table.getSelectionModel().getSelectedItem();
            if (selectedTLD != null) {
                handleEditButton();
                tfTLD.setText(selectedTLD.getName());
                tfPrice.setText(String.valueOf(selectedTLD.getPrice()));
                tfPrice.setEditable(true);
                tfPrice.setFocusTraversable(true);
            }
        });

        tfPrice.textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                if (newValue.isBlank()) {
                    newValue = "0";
                }
                Integer value = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                tfPrice.setText(oldValue);
            }
        });

        tfPrice.setOnAction(ActionEvent -> {
            handleSaveButton();
        });
    }

    private void initTable() {
        // set cell value factory
        colID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colText.setCellValueFactory(new PropertyValueFactory<>("name"));

        colPrice.setCellFactory(MoneyCellFactory.forTableColumn());
        // set text when no data
        table.setPlaceholder(new Label(bundle.getString("placeHolder.tableEmpty")));

        updateTable();
    }

    // set data for table
    private void updateTable() {
        List<TLDViewModel> tldList = getTLDList();
        table.getItems().clear();
        table.getItems().addAll(tldList);

        // set filter for table
        FilteredList<TLDViewModel> filteredData = new FilteredList<>(table.getItems(), b -> true);
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(tld -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return tld.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });
        table.setItems(filteredData);
    }

    // handle button action
    private void handleCancelButton() {
        btSave.setVisible(false);
        btCancel.setVisible(false);

        tfTLD.clear();
        tfPrice.clear();
    }

    // handle save button action
    private void handleSaveButton() {
        String tldName = tfTLD.getText();
        String tldPrice = tfPrice.getText();

        if (tldPrice.isEmpty() || !CheckingUtils.numberCheck(tldPrice)) {
            SceneManager.getInstance().showDialog(Alert.AlertType.WARNING, bundle.getString("warning"), null, bundle.getString("error.noSelect"));
            return;
        }

        if (Integer.parseInt(tldPrice) == selectedTLD.getPrice()) {
            handleCancelButton();
            return;
        }

        Optional<ButtonType> buttonType = SceneManager.getInstance().showDialog(Alert.AlertType.CONFIRMATION, bundle.getString("save"), null, bundle.getString("notice.save"));
        if (buttonType.isPresent() && buttonType.get() == ButtonType.NO) {
            return;
        }
        // get selected TLD
        TLDViewModel selectedTLD = table.getSelectionModel().getSelectedItem();
        if (selectedTLD != null) {
            JSONObject request = new JSONObject();
            request.put("id", selectedTLD.getID());
            request.put("TLD_text", tldName);
            request.put("price", Integer.parseInt(tldPrice));
            // update TLD
            JSONObject respond = tldServices.updateTLD(request);
            if (respond.get("status").equals("failed")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(bundle.getString("error"));
                alert.setHeaderText(bundle.getString("notice.updateTLDFailed"));
                alert.showAndWait();
            } else {
                // update table
                selectedTLD.setPrice(Integer.parseInt(tldPrice));
            }
        }
        MainController.getInstance().refresh();
    }

    // handle edit button action
    private void handleEditButton() {
        TLDViewModel selectedTLD = table.getSelectionModel().getSelectedItem();
        if (selectedTLD != null) {
            tfTLD.setText(selectedTLD.getName());
            tfPrice.setText(String.valueOf(selectedTLD.getPrice()));

            btSave.setVisible(true);
            btCancel.setVisible(true);

            tfPrice.setEditable(true);
        } else {
            // show alert if no TLD is selected
            SceneManager.getInstance().showDialog(Alert.AlertType.WARNING, bundle.getString("warning"), null, bundle.getString("error.noSelect"));
        }
    }

    // handle add button action
    private void handleAddButton() {
        tfTLD.clear();
        tfPrice.clear();

        btSave.setVisible(true);
        btCancel.setVisible(true);

        tfTLD.setEditable(true);
        tfPrice.setEditable(true);
    }

    // get all payment history
    private List<TLDViewModel> getTLDList() {
        List<TLDViewModel> tldList = new ArrayList<>();
        JSONObject response = tldServices.getAllTLD();
        JSONArray jsonArray = response.getJSONArray("tld");
        for (Object o : jsonArray) {
            /// parse json object
            JSONObject jsonObject = (JSONObject) o;

            TLDViewModel tld = new TLDViewModel();
            tld.setID(jsonObject.getInt("id"));
            tld.setName(jsonObject.getString("TLD_text"));
            tld.setPrice(jsonObject.getInt("price"));
            tldList.add(tld);
        }

        return tldList;
    }
}