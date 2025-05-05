package com.utc2.domainstore.utils;

import com.utc2.domainstore.entity.view.ACCOUNT_STATUS;
import com.utc2.domainstore.view.ConfigManager;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class AccountStatusCellFactory {
    public static <T> Callback<TableColumn<T, ACCOUNT_STATUS>, TableCell<T, ACCOUNT_STATUS>> forTableColumn() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(ACCOUNT_STATUS item, boolean empty) {
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
