package com.utc2.domainstore.utils;

import com.utc2.domainstore.view.ConfigManager;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.NumberFormat;

// formatting money values in a table cell
// import setting money format by locale
public class MoneyCellFactory {
    private static NumberFormat formatter = ConfigManager.getInstance().getNumberFormatter();

    public static <T> Callback<TableColumn<T, Integer>, TableCell<T, Integer>> forTableColumn() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // format the item as currency
                    Double value = Double.valueOf(item);
                    value = value / ConfigManager.getInstance().getRate(ConfigManager.getInstance().getSetting("language", "English"));
                    setText(formatter.format(value));
                }
            }
        };
    }
}
