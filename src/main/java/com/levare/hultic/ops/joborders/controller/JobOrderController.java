package com.levare.hultic.ops.joborders.controller;

import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;
import com.levare.hultic.ops.joborders.service.JobOrderService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * JavaFX controller for displaying and managing JobOrders.
 */
public class JobOrderController {

    private JobOrderService jobOrderService;

    @FXML private TableView<JobOrder> jobOrderTable;
    @FXML private TableColumn<JobOrder, Long> idColumn;
    @FXML private TableColumn<JobOrder, JobOrderStatus> statusColumn;
    @FXML private TableColumn<JobOrder, String> commentsColumn;

    @FXML private ComboBox<JobOrderStatus> statusFilterCombo;

    @FXML private Button refreshButton;
    @FXML private Button deleteButton;
    @FXML private Button createButton;
    @FXML private Button updateButton;
    @FXML private Button statusButton;

    public void setJobOrderService(JobOrderService service) {
        this.jobOrderService = service;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleLongProperty(cell.getValue().getId()).asObject());
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getStatus()));
        commentsColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getComments()));

        statusFilterCombo.setItems(FXCollections.observableArrayList(JobOrderStatus.values()));
        refreshButton.setOnAction(e -> refreshTable());
        deleteButton.setOnAction(e -> deleteSelected());
        createButton.setOnAction(e -> createNew());
        updateButton.setOnAction(e -> updateSelected());
        statusButton.setOnAction(e -> changeStatusSelected());
    }

    private void refreshTable() {
        List<JobOrder> jobOrders;
        JobOrderStatus status = statusFilterCombo.getValue();

        if (status != null) {
            jobOrders = jobOrderService.getByStatus(status);
        } else {
            jobOrders = jobOrderService.getAll();
        }

        jobOrderTable.setItems(FXCollections.observableArrayList(jobOrders));
    }

    private void deleteSelected() {
        JobOrder selected = jobOrderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            jobOrderService.delete(selected.getId());
            refreshTable();
        } else {
            showAlert("No selection", "Please select a job order to delete.");
        }
    }

    private void createNew() {
        JobOrder newOrder = new JobOrder();
        newOrder.setStatus(JobOrderStatus.CREATED);
        newOrder.setComments("New Job Order");
        // Note: Set item, workOrder, and user manually or through dialog
        jobOrderService.create(newOrder);
        refreshTable();
    }

    private void updateSelected() {
        JobOrder selected = jobOrderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setComments(selected.getComments() + " (updated)");
            jobOrderService.update(selected.getId(), selected);
            refreshTable();
        } else {
            showAlert("No selection", "Please select a job order to update.");
        }
    }

    private void changeStatusSelected() {
        JobOrder selected = jobOrderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus(JobOrderStatus.IN_PROGRESS);
            jobOrderService.changeStatus(selected.getId(), JobOrderStatus.IN_PROGRESS);
            refreshTable();
        } else {
            showAlert("No selection", "Please select a job order to change status.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
