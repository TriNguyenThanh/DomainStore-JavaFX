package com.utc2.domainstore.utils;

import com.utc2.domainstore.entity.view.STATUS;
import com.utc2.domainstore.view.ConfigManager;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class StatusCellFactory {
    public static <T> Callback<TableColumn<T, STATUS>, TableCell<T, STATUS>> forTableColumn() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(STATUS item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // format the item as currency
                    setText(ConfigManager.getInstance().getLanguageBundle().getString("status." + String.valueOf(item).toLowerCase()));
                }
            }
        };
    }
}
