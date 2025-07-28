package com.levare.hultic.ops.common;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Утилитные методы для работы с датами и временем.
 */
public final class DateUtils {

    private DateUtils() {
        // приватный конструктор — класс только для статических методов
    }

    /**
     * Преобразует миллисекунды от эпохи (Unix time) в объект LocalDate
     * в зоне по умолчанию.
     *
     * @param epochMilli время в миллисекундах от 1970-01-01T00:00:00Z
     * @return LocalDate в системной временной зоне
     */
    public static LocalDate epochToLocalDate(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Открывает модальный диалог с DatePicker и возвращает выбранную дату.
     * Если пользователь отменил или не выбрал дату — диалог будет повторяться
     * до тех пор, пока не будет выбрана корректная дата.
     *
     * @param title заголовок диалога
     * @return выбранная LocalDate
     */
    public static LocalDate pickDate(String title) {
        while (true) {
            Dialog<LocalDate> dialog = new Dialog<>();
            dialog.setTitle(title);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            DatePicker picker = new DatePicker(LocalDate.now());
            dialog.getDialogPane().setContent(picker);

            dialog.setResultConverter(button -> button == ButtonType.OK ? picker.getValue() : null);

            LocalDate result = dialog.showAndWait().orElse(null);
            if (result != null) {
                return result;
            }

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation");
            alert.setHeaderText(null);
            alert.setContentText("Please select a date.");
            alert.showAndWait();
        }
    }
}