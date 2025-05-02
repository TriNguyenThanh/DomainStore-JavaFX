package com.utc2.domainstore.utils;

import com.utc2.domainstore.view.ConfigManager;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateCellFactory {
    private static final DateTimeFormatter formatter = ConfigManager.getInstance().getDateTimeFormatter();

    public static <T> Callback<TableColumn<T, LocalDate>, TableCell<T, LocalDate>> forTableColumn() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        };
    }
}