package com.utc2.domainstore.utils;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SpinnerCellFactory<T> extends TableCell<T, Integer> {
    private final Spinner<Integer> spinner;

    @Override
    protected void updateItem(Integer value, boolean empty) {
        super.updateItem(value, empty);
        if (empty || value == null) {
            setGraphic(null);
        } else {
            spinner.getValueFactory().setValue(value);
            setGraphic(spinner);
        }
    }

    public static <T> Callback<TableColumn<T, Integer>, TableCell<T, Integer>> forTableColumn(
            int min, int max, int step, Function<T, Integer> getter, BiConsumer<T, Integer> setter) {
        return col -> {
            col.setEditable(true);
            return new SpinnerCellFactory<>(min, max, step, getter, setter);
        };
    }

    public SpinnerCellFactory(int min, int max, int step, Function<T, Integer> getter, BiConsumer<T, Integer> setter) {
        spinner = new Spinner<>(min, max, min, step);
        spinner.setEditable(true);

        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (getTableRow() != null && getTableRow().getItem() != null) {
                T item = getTableRow().getItem();
                if (item != null) {
                    setter.accept(item, newValue); // Cập nhật mô hình dữ liệu
                    commitEdit(newValue); // Xác nhận chỉnh sửa
                }
            }
            System.out.println("value input: " + spinner.getEditor().getText());
        });

        spinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                System.out.println("old: " + oldValue + " new: " + newValue);
                if (newValue.isBlank()) newValue = "0";

                int value = Integer.parseInt(newValue);

                if (value > 10) {
                    value = 10;
                }
                spinner.getEditor().setText(String.valueOf(value));
            } catch (NumberFormatException e) {
                // Restore previous valid value
                spinner.getEditor().setText(oldValue);
            }
        });

        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    public static <T> Callback<TableColumn<T, Integer>, TableCell<T, Integer>> forTableColumn(
            int min, int max, int step, Function<T, Integer> getter, BiConsumer<T, Integer> setter, Runnable onValueChange) {
        return col -> {
            col.setEditable(true);
            return new SpinnerCellFactory<>(min, max, step, getter, setter, onValueChange);
        };
    }

    public SpinnerCellFactory(int min, int max, int step, Function<T, Integer> getter, BiConsumer<T, Integer> setter, Runnable onValueChange) {
        spinner = new Spinner<>(min, max, min, step);
        spinner.setEditable(true);

        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (getTableRow() != null && getTableRow().getItem() != null) {
                T item = getTableRow().getItem();
                if (item != null) {
                    setter.accept(item, newValue); // Cập nhật mô hình dữ liệu
                    commitEdit(newValue); // Xác nhận chỉnh sửa
                    if (onValueChange != null) {
                        onValueChange.run(); // Trigger the callback
                    }
                }
            }
        });

        spinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                System.out.println("old: " + oldValue + " new: " + newValue);
                if (newValue.isBlank()) newValue = "0";

                int value = Integer.parseInt(newValue);

                if (value > 10) {
                    value = 10;
                }
                spinner.getEditor().setText(String.valueOf(value));
            } catch (NumberFormatException e) {
                // Restore previous valid value
                spinner.getEditor().setText(oldValue);
            }
        });

        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }
}