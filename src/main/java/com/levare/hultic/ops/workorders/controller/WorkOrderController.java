package com.levare.hultic.ops.workorders.controller;

import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;

import java.time.LocalDate;
import java.util.List;

/**
 * JavaFX controller for managing WorkOrders with full CRUD support.
 */
public class WorkOrderController {

    private WorkOrderService workOrderService;

    @FXML private TableView<WorkOrder> workOrderTable;
    @FXML private TableColumn<WorkOrder, Long> idColumn;
    @FXML private TableColumn<WorkOrder, String> numberColumn;
    @FXML private TableColumn<WorkOrder, String> clientColumn;
    @FXML private TableColumn<WorkOrder, String> wellColumn;
    @FXML private TableColumn<WorkOrder, LocalDate> requestDateColumn;
    @FXML private TableColumn<WorkOrder, LocalDate> deliveryDateColumn;
    @FXML private TableColumn<WorkOrder, WorkOrderStatus> statusColumn;
    @FXML private TableColumn<WorkOrder, String> requestorColumn;
    @FXML private TableColumn<WorkOrder, String> commentsColumn;

    @FXML private TextField numberField;
    @FXML private TextField wellField;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private TextArea commentsArea;
    @FXML private ComboBox<WorkOrderStatus> statusFilterCombo;

    @FXML private Button createButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button refreshButton;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActions();
        setupContextMenu();
        // refreshTable(); — удалено, т.к. сервис ещё не установлен
    }

    public void setWorkOrderService(WorkOrderService service) {
        this.workOrderService = service;
        if (service != null) {
        } else {
            System.err.println("⚠️ WorkOrderService is null — table not refreshed");
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cell -> new SimpleLongProperty(cell.getValue().getId()).asObject());
        numberColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getWorkOrderNumber()));
        clientColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getClient() != null ? cell.getValue().getClient().name() : "")
        );
        wellColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getWell()));
        requestDateColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getRequestDate()));
        deliveryDateColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDeliveryDate()));
        statusColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getStatus()));
        requestorColumn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getRequestor() != null ? cell.getValue().getRequestor().getName() : "")
        );
        commentsColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getComments()));
    }

    private void setupActions() {
        if (statusFilterCombo != null) {
            statusFilterCombo.setItems(FXCollections.observableArrayList(WorkOrderStatus.values()));
        }

        if (refreshButton != null) refreshButton.setOnAction(e -> refreshTable());
        if (createButton != null) createButton.setOnAction(e -> createWorkOrder());
        if (updateButton != null) updateButton.setOnAction(e -> updateWorkOrder());
        if (deleteButton != null) deleteButton.setOnAction(e -> deleteWorkOrder());
        if (clearButton != null) clearButton.setOnAction(e -> clearForm());

        workOrderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                fillForm(newSel);
            }
        });
    }

    private void setupContextMenu() {
        workOrderTable.setRowFactory(tv -> {
            TableRow<WorkOrder> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem newItem = new MenuItem("New WorkOrder");
            newItem.setOnAction(e -> clearForm());

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(e -> {
                WorkOrder selected = row.getItem();
                if (selected != null) {
                    fillForm(selected);
                }
            });

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> {
                WorkOrder selected = row.getItem();
                if (selected != null && workOrderService != null) {
                    workOrderService.delete(selected.getId());
                    refreshTable();
                    clearForm();
                }
            });

            contextMenu.getItems().addAll(newItem, editItem, deleteItem);

            row.setOnContextMenuRequested((ContextMenuEvent event) -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            return row;
        });
    }

    public void refreshTable() {
        if (workOrderService == null) return;

        List<WorkOrder> workOrders;
        WorkOrderStatus selectedStatus = statusFilterCombo != null ? statusFilterCombo.getValue() : null;

        if (selectedStatus != null) {
            workOrders = workOrderService.getByStatus(selectedStatus);
        } else {
            workOrders = workOrderService.getAll();
        }

        workOrderTable.setItems(FXCollections.observableArrayList(workOrders));
    }

    private void createWorkOrder() {
        WorkOrder order = new WorkOrder();
        order.setWorkOrderNumber(numberField.getText().trim());
        order.setWell(wellField.getText().trim());
        order.setDeliveryDate(deliveryDatePicker.getValue());
        order.setComments(commentsArea.getText().trim());
        if (workOrderService != null) {
            workOrderService.create(order);
            refreshTable();
            clearForm();
        }
    }

    private void updateWorkOrder() {
        WorkOrder selected = workOrderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No selection", "Please select a work order to update.");
            return;
        }

        selected.setWorkOrderNumber(numberField.getText().trim());
        selected.setWell(wellField.getText().trim());
        selected.setDeliveryDate(deliveryDatePicker.getValue());
        selected.setComments(commentsArea.getText().trim());

        if (workOrderService != null) {
            workOrderService.update(selected);
            refreshTable();
        }
    }

    private void deleteWorkOrder() {
        WorkOrder selected = workOrderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No selection", "Please select a work order to delete.");
            return;
        }

        if (workOrderService != null) {
            workOrderService.delete(selected.getId());
            refreshTable();
            clearForm();
        }
    }

    private void clearForm() {
        if (numberField != null) numberField.clear();
        if (wellField != null) wellField.clear();
        if (deliveryDatePicker != null) deliveryDatePicker.setValue(null);
        if (commentsArea != null) commentsArea.clear();
        workOrderTable.getSelectionModel().clearSelection();
    }

    private void fillForm(WorkOrder order) {
        if (numberField != null) numberField.setText(order.getWorkOrderNumber());
        if (wellField != null) wellField.setText(order.getWell());
        if (deliveryDatePicker != null) deliveryDatePicker.setValue(order.getDeliveryDate());
        if (commentsArea != null) commentsArea.setText(order.getComments());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
