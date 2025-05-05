package com.utc2.domainstore.utils;

import com.utc2.domainstore.view.ConfigManager;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class YearCellFactory {
    public static <T> Callback<TableColumn<T, Integer>, TableCell<T, Integer>> forTableColumn() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // format the item as currency
                    setText(item + " " + ConfigManager.getInstance().getLanguageBundle().getString("year"));
                }
            }
        };
    }
}
