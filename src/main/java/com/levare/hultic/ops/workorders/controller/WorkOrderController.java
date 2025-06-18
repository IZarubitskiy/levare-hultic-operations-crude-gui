package com.levare.hultic.ops.workorders.controller;

import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;

/**
 * JavaFX controller for managing WorkOrders with full CRUD support.
 * Получает зависимости через конструктор от AppControllerFactory.
 */
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final Callback<Class<?>, Object> controllerFactory;

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

    public WorkOrderController(WorkOrderService workOrderService,
                               Callback<Class<?>, Object> controllerFactory) {
        this.workOrderService = workOrderService;
        this.controllerFactory = controllerFactory;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActions();
        setupContextMenu();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getId())
        );
        numberColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getWorkOrderNumber())
        );
        clientColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getClient() != null
                                ? cell.getValue().getClient().name() : ""
                )
        );
        wellColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getWell())
        );
        requestDateColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getRequestDate())
        );
        deliveryDateColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getDeliveryDate())
        );
        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getStatus())
        );
        requestorColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getRequestor() != null
                                ? cell.getValue().getRequestor().getName() : ""
                )
        );
        commentsColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getComments())
        );
    }

    private void setupActions() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(WorkOrderStatus.values()));
        refreshButton.setOnAction(e -> refreshTable());
        createButton.setOnAction(e -> handleNewWorkOrder());
        updateButton.setOnAction(e -> updateWorkOrder());
        deleteButton.setOnAction(e -> deleteWorkOrder());
        clearButton.setOnAction(e -> clearForm());

        workOrderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) fillForm(newSel);
        });
    }

    private void setupContextMenu() {
        workOrderTable.setRowFactory(tv -> {
            TableRow<WorkOrder> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            MenuItem newItem = new MenuItem("New WorkOrder");
            newItem.setOnAction(e -> clearForm());

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(e -> fillForm(row.getItem()));

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> {
                WorkOrder order = row.getItem();
                if (order != null) {
                    workOrderService.delete(order.getId());
                    refreshTable();
                    clearForm();
                }
            });

            menu.getItems().addAll(newItem, editItem, deleteItem);
            row.setOnContextMenuRequested((ContextMenuEvent e) -> {
                if (!row.isEmpty()) menu.show(row, e.getScreenX(), e.getScreenY());
            });
            return row;
        });
    }

    public void refreshTable() {
        List<WorkOrder> orders;
        WorkOrderStatus filter = statusFilterCombo.getValue();
        orders = filter != null
                ? workOrderService.getByStatus(filter)
                : workOrderService.getAll();
        workOrderTable.setItems(FXCollections.observableArrayList(orders));
    }

    private void handleNewWorkOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new_workorder.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent form = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("New Work Order");
            dialog.getIcons().add(new Image(
                    getClass().getResourceAsStream("/icons/new_workorder.png")
            ));
            dialog.setScene(new Scene(form));
            dialog.showAndWait();

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateWorkOrder() {
        WorkOrder sel = workOrderTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("No selection", "Please select a work order to update.");
            return;
        }
        sel.setWorkOrderNumber(numberField.getText().trim());
        sel.setWell(wellField.getText().trim());
        sel.setDeliveryDate(deliveryDatePicker.getValue());
        sel.setComments(commentsArea.getText().trim());
        workOrderService.update(sel);
        refreshTable();
    }

    private void deleteWorkOrder() {
        WorkOrder sel = workOrderTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("No selection", "Please select a work order to delete.");
            return;
        }
        workOrderService.delete(sel.getId());
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
