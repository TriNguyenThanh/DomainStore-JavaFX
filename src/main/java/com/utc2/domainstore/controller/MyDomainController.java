package com.utc2.domainstore.controller;

import com.utc2.domainstore.entity.view.DomainViewModel;
import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.service.DomainServices;
import com.utc2.domainstore.service.IDomain;
import com.utc2.domainstore.service.ITransactionService;
import com.utc2.domainstore.service.TransactionService;
import com.utc2.domainstore.utils.LocalDateCellFactory;
import com.utc2.domainstore.utils.MoneyCellFactory;
import com.utc2.domainstore.utils.YearCellFactory;
import com.utc2.domainstore.view.ConfigManager;
import com.utc2.domainstore.view.SceneManager;
import com.utc2.domainstore.view.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDomainController implements Initializable {
    private ResourceBundle bundle;
    private AtomicInteger renew = new AtomicInteger(0);
    private Integer previousValue = 0;
    private ITransactionService transactionService = new TransactionService();
    @FXML
    private TableView<DomainViewModel> tbDomain;
    @FXML
    private TableColumn<DomainViewModel, String> colDomain;
    @FXML
    private TableColumn<DomainViewModel, Integer> colPrice, colYear;
    @FXML
    private TableColumn<DomainViewModel, LocalDateTime> colDate;
    @FXML
    private Button btUpdate, btComfirm;
    @FXML
    private TextField tfSearch;
    private Spinner<Integer> tfYears;

    @FXML
    private void handleButton(ActionEvent e) {
        if (e.getSource() == btUpdate) {
            handleUpdate();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        initTable();
    }

    private void handleUpdate() {
        DomainViewModel domainViewModel = null;
        domainViewModel = tbDomain.getSelectionModel().getSelectedItem();
        if (domainViewModel == null) {
            SceneManager.getInstance().showDialog(Alert.AlertType.WARNING, bundle.getString("warning"), null, bundle.getString("error.noSelect"));
        } else {
            getYear();
            Optional<ButtonType> buttonType = SceneManager.getInstance().showDialog(Alert.AlertType.CONFIRMATION, bundle.getString("information"), null, bundle.getString("notice.domainRenew"));
            if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
                createTransactions(domainViewModel);
            }
        }
    }

    private void initTable() {
        // Set up the table columns
        colDomain.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("years"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        colDate.setCellFactory(LocalDateCellFactory.forTableColumn());
        colPrice.setCellFactory(MoneyCellFactory.forTableColumn());
        colYear.setCellFactory(YearCellFactory.forTableColumn());

        tbDomain.setPlaceholder(new Label(bundle.getString("placeHolder.tableEmpty")));

        updateTable();
    }

    private void updateTable() {
        FilteredList<DomainViewModel> filteredData = new FilteredList<>(FXCollections.observableArrayList(getData()), p -> true);
        // Set the filter predicate
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(domain -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all items
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return domain.getName().toLowerCase().contains(lowerCaseFilter) ||
                        domain.getPrice().toString().contains(lowerCaseFilter);
            });
        });
        tbDomain.setItems(filteredData);
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
                LocalDateTime date = LocalDateTime.parse(domainObject.opt("active_date").toString(), ConfigManager.getInstance().getParser());

                DomainViewModel domainViewModel = new DomainViewModel(name, STATUS.valueOf(status.toUpperCase()), price, years, date);
                list.add(domainViewModel);
            }
        }
        return list;
    }

    private void validateInput(Spinner<Integer> spinner) {
        String text = spinner.getEditor().getText();
        try {
            int value = Integer.parseInt(text);
            if (value < 0) {
                spinner.getValueFactory().setValue(0);
                spinner.getEditor().setText("0");
            } else if (value > 10) {
                spinner.getValueFactory().setValue(10);
                spinner.getEditor().setText("10");
            } else {
                spinner.getValueFactory().setValue(value);
                spinner.getEditor().setText(String.valueOf(value));
            }
        } catch (NumberFormatException e) {
            spinner.getValueFactory().setValue(0);
            spinner.getEditor().setText("0");
        }
        spinner.getEditor().positionCaret(spinner.getEditor().getText().length());
    }

    private void getYear() {
        renew.set(0);
        tfYears = new Spinner<>(0, 10, 1);
        tfYears.setEditable(true);
        tfYears.getEditor().positionCaret(tfYears.getEditor().getText().length());

        //loc key
        tfYears.getEditor().addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9]")) {
                event.consume();
            }
        });

        tfYears.getEditor().setOnKeyReleased(event -> {
            String text = tfYears.getEditor().getText();
            if (!text.matches("\\d*")) return;

            validateInput(tfYears);
            System.out.println(tfYears.getValue());
        });

        // Khi mất focus, kiểm tra giá trị nhập
        tfYears.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                validateInput(tfYears);
            }
        });

        // Khi thay đổi bằng nút mũi tên
        tfYears.valueProperty().addListener((obs, oldVal, newVal) -> {
            previousValue = newVal; // Lưu lại giá trị hợp lệ gần nhất
        });

        btComfirm = new Button(bundle.getString("confirm"));
        btComfirm.setOnAction(actionEvent -> {
            renew.set(tfYears.getValue());
            ((Stage) btComfirm.getScene().getWindow()).close();
        });

        HBox content = new HBox(20);
        content.setPadding(new Insets(20));
        content.getChildren().add(tfYears);
        content.getChildren().add(btComfirm);

        Scene scene = new Scene(content);
        scene.getStylesheets().add(getClass().getResource("/style/subwindow.css").toExternalForm());

        Stage secondStage = new Stage();
        secondStage.setScene(scene);
        secondStage.setResizable(false);
        secondStage.setTitle(bundle.getString("year"));

        secondStage.initModality(Modality.WINDOW_MODAL);
        secondStage.initOwner(SceneManager.getInstance().getStage());
        secondStage.showAndWait();
    }

    private void createTransactions(DomainViewModel domainViewModel) {
        /* request: user_id
                    is_renewal (1: renew, 0: new)
                    domains
        response: transactionId (String), total(int), status (success / failed)
        domains: name, status, price, years
        */
        JSONObject domain = new JSONObject();
        domain.put("name", domainViewModel.getName());
        domain.put("status", domainViewModel.getStatus());
        domain.put("price", domainViewModel.getPrice());
        domain.put("years", renew.get());

        JSONArray domains = new JSONArray();
        domains.put(domain);

        JSONObject request = new JSONObject();
        request.put("user_id", UserSession.getInstance().getUserId());
        request.put("is_renewal", 1);
        request.put("domains", domains);

        JSONObject response = null;
        try {
            response = transactionService.createTransaction(request);
            if (response.getString("status").equals("success")) {
                SceneManager.getInstance().showDialog(Alert.AlertType.INFORMATION, bundle.getString("success"), null, bundle.getString("notice.createTransactionSuccess") + ": " + response.getString("transactionId"));

                String currentFXMLPath = "/fxml/transaction.fxml";
                MainController.getInstance().setCurrentFxmlPath(currentFXMLPath);
                MainController.getInstance().load(currentFXMLPath, true);
            }
        } catch (IOException e) {
            SceneManager.getInstance().showDialog(Alert.AlertType.WARNING, bundle.getString("warning"), null, bundle.getString("notice.serverError"));
        }
    }
}