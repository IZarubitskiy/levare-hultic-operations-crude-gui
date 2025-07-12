package com.levare.hultic.ops.tracking.controller;

import com.levare.hultic.ops.tracking.model.ActionTarget;
import com.levare.hultic.ops.tracking.model.ActionType;
import com.levare.hultic.ops.tracking.model.TrackingRecord;
import com.levare.hultic.ops.tracking.service.TrackingRecordService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class TrackingRecordController {

    private final TrackingRecordService trackingService;

    public TrackingRecordController(TrackingRecordService trackingService) {
        this.trackingService = trackingService;
    }

    @FXML
    private TableView<TrackingRecord> trackingTable;
    @FXML
    private TableColumn<TrackingRecord, Long> idColumn;
    @FXML
    private TableColumn<TrackingRecord, LocalDate> dateColumn;
    @FXML
    private TableColumn<TrackingRecord, ActionTarget> targetColumn;
    @FXML
    private TableColumn<TrackingRecord, ActionType> typeColumn;
    @FXML
    private TableColumn<TrackingRecord, Long> woColumn;
    @FXML
    private TableColumn<TrackingRecord, Long> joColumn;
    @FXML
    private TableColumn<TrackingRecord, String> pnColumn;
    @FXML
    private TableColumn<TrackingRecord, String> snColumn;
    @FXML
    private TableColumn<TrackingRecord, String> descColumn;
    @FXML
    private TableColumn<TrackingRecord, String> reasonColumn;

    @FXML
    private DatePicker fromDatePicker, toDatePicker;
    @FXML
    private ComboBox<ActionTarget> targetFilterCombo;
    @FXML
    private ComboBox<ActionType> typeFilterCombo;
    @FXML
    private TextField workOrderField, jobOrderField, pnField, snField;
    @FXML
    private Button searchButton, clearButton;

    @FXML
    public void initialize() {
        // колонки
        idColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        dateColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getRecordDate()));
        targetColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getActionTarget()));
        typeColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getActionType()));
        woColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTargetWorkOrderId()));
        joColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTargetJobOrderId()));
        pnColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTargetPN()));
        snColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTargetSN()));
        descColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTargetDescription()));
        reasonColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getReason()));

        // фильтры
        targetFilterCombo.setItems(FXCollections.observableArrayList(ActionTarget.values()));
        typeFilterCombo.setItems(FXCollections.observableArrayList(ActionType.values()));

        searchButton.setOnAction(e -> refreshTable());
        clearButton.setOnAction(e -> {
            fromDatePicker.setValue(null);
            toDatePicker.setValue(null);
            targetFilterCombo.getSelectionModel().clearSelection();
            typeFilterCombo.getSelectionModel().clearSelection();
            workOrderField.clear();
            jobOrderField.clear();
            pnField.clear();
            snField.clear();
            refreshTable();
        });

        // первый прогон
        refreshTable();
    }

    public void refreshTable() {
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();
        ActionTarget at = targetFilterCombo.getValue();
        ActionType ty = typeFilterCombo.getValue();

        Long wo = parseLong(workOrderField.getText());
        Long jo = parseLong(jobOrderField.getText());
        String pn = pnField.getText().trim();
        String sn = snField.getText().trim();

        List<TrackingRecord> results = trackingService.findByCriteria(
                from, to, at, ty, wo, jo,
                pn.isEmpty() ? null : pn,
                sn.isEmpty() ? null : sn
        );
        trackingTable.setItems(FXCollections.observableArrayList(results));
    }

    private Long parseLong(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
