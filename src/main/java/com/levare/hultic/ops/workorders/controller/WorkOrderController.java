package com.levare.hultic.ops.workorders.controller;

import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * JavaFX controller for managing WorkOrders with full CRUD support.
 */
public class WorkOrderController {

    private WorkOrderService workOrderService;

    @FXML private TableView<WorkOrder> workOrderTable;
    @FXML private TableColumn<WorkOrder, Long> idColumn;
    @FXML private TableColumn<WorkOrder, String> numberColumn;
    @FXML private TableColumn<WorkOrder, WorkOrderStatus> statusColumn;
    @FXML private TableColumn<WorkOrder, String> wellColumn;

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

    public void setWorkOrderService(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleLongProperty(cell.getValue().getId()).asObject());
        numberColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getWorkOrderNumber()));
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getStatus()));
        wellColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getWell()));

        statusFilterCombo.setItems(FXCollections.observableArrayList(WorkOrderStatus.values()));
        refreshButton.setOnAction(e -> refreshTable());
        createButton.setOnAction(e -> createWorkOrder());
        updateButton.setOnAction(e -> updateWorkOrder());
        deleteButton.setOnAction(e -> deleteWorkOrder());
        clearButton.setOnAction(e -> clearForm());

        workOrderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                fillForm(newSel);
            }
        });

        refreshTable();
    }

    private void refreshTable() {
        List<WorkOrder> workOrders;
        WorkOrderStatus selectedStatus = statusFilterCombo.getValue();
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
        // requestor and client should be assigned before creation
        workOrderService.create(order);
        refreshTable();
        clearForm();
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
        workOrderService.update(selected);
        refreshTable();
    }

    private void deleteWorkOrder() {
        WorkOrder selected = workOrderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No selection", "Please select a work order to delete.");
            return;
        }

        workOrderService.delete(selected.getId());
        refreshTable();
        clearForm();
    }

    private void clearForm() {
        numberField.clear();
        wellField.clear();
        deliveryDatePicker.setValue(null);
        commentsArea.clear();
        workOrderTable.getSelectionModel().clearSelection();
    }

    private void fillForm(WorkOrder order) {
        numberField.setText(order.getWorkOrderNumber());
        wellField.setText(order.getWell());
        deliveryDatePicker.setValue(order.getDeliveryDate());
        commentsArea.setText(order.getComments());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
