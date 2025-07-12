package com.levare.hultic.ops.workorders.controller;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.joborders.controller.NewJobOrderController;
import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.users.service.UserService;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Work Orders and inline creation of JobOrders via 'Create JO' column.
 */
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final UserService userService;
    private final ItemService itemService;
    private final Callback<Class<?>, Object> controllerFactory;

    @FXML
    private Label detailNumberLabel;
    @FXML
    private Label detailClientLabel;
    @FXML
    private Label detailWellLabel;
    @FXML
    private Label detailRequestDateLabel;
    @FXML
    private Label detailDeliveryDateLabel;
    @FXML
    private Label detailStatusLabel;
    @FXML
    private Label detailRequestorLabel;
    @FXML
    private TextArea detailCommentsArea;

    @FXML
    private TableView<Item> equipmentTable;
    @FXML
    private TableColumn<Item, String> eqPartColumn;
    @FXML
    private TableColumn<Item, String> eqDescColumn;
    @FXML
    private TableColumn<Item, String> eqSerialColumn;
    @FXML
    private TableColumn<Item, String> eqConditionColumn;
    @FXML
    private TableColumn<Item, String> eqStatusColumn;
    @FXML
    private TableColumn<Item, String> eqJobOrderColumn;
    @FXML
    private TableColumn<Item, Void> assignColumn;

    @FXML
    private TableView<WorkOrder> workOrderTable;
    @FXML
    private TableColumn<WorkOrder, Long> idColumn;
    @FXML
    private TableColumn<WorkOrder, String> numberColumn;
    @FXML
    private TableColumn<WorkOrder, String> clientColumn;
    @FXML
    private TableColumn<WorkOrder, String> wellColumn;
    @FXML
    private TableColumn<WorkOrder, LocalDate> requestDateColumn;
    @FXML
    private TableColumn<WorkOrder, LocalDate> deliveryDateColumn;
    @FXML
    private TableColumn<WorkOrder, WorkOrderStatus> statusColumn;
    @FXML
    private TableColumn<WorkOrder, String> requestorColumn;
    @FXML
    private TableColumn<WorkOrder, String> commentsColumn;

    @FXML
    private ComboBox<WorkOrderStatus> statusFilterCombo;
    @FXML
    private Button createButton, updateButton, deleteButton, clearButton, refreshButton;

    @FXML
    private TextField numberField, wellField;
    @FXML
    private DatePicker deliveryDatePicker;
    @FXML
    private TextArea commentsArea;

    public WorkOrderController(
            WorkOrderService workOrderService,
            UserService userService,
            ItemService itemService,
            Callback<Class<?>, Object> controllerFactory
    ) {
        this.workOrderService = workOrderService;
        this.userService = userService;
        this.itemService = itemService;
        this.controllerFactory = controllerFactory;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupEquipmentColumns();
        setupAssignColumn();
        setupActions();
        setupContextMenu();
        refreshTable();
    }

    private void setupAssignColumn() {
        assignColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Create JO");

            {
                btn.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    WorkOrder order = workOrderTable.getSelectionModel().getSelectedItem();
                    if (order != null) {
                        openJobOrderDialog(order.getId(), item.getId());
                        refreshTable();
                    } else {
                        alert("No WorkOrder selected", "Select a Work Order first.");
                    }
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Item item = getTableView().getItems().get(getIndex());
                    btn.setDisable(item.getJobOrderId() != null);
                    setGraphic(btn);
                }
            }
        });
    }

    private void openJobOrderDialog(Long workOrderId, Long itemId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/new_job_order.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent form = loader.load();

            NewJobOrderController ctrl = loader.getController();
            ctrl.initForWorkAndItem(workOrderId, itemId);

            Stage dlg = new Stage();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("New Job Order");
            dlg.setScene(new Scene(form));
            dlg.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            alert("Error", "Cannot open Job Order dialog:\n" + ex.getMessage());
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        numberColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getWorkOrderNumber()));
        clientColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getClient() != null ? c.getValue().getClient().name() : ""));
        wellColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getWell()));
        requestDateColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getRequestDate()));
        deliveryDateColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getDeliveryDate()));
        deliveryDateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(date.toString());
                    long days = ChronoUnit.DAYS.between(LocalDate.now(), date);
                    if (days <= 2) setStyle("-fx-background-color: tomato;");
                    else if (days <= 3) setStyle("-fx-background-color: khaki;");
                    else setStyle("");
                }
            }
        });
        statusColumn.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getStatus()));
        requestorColumn.setCellValueFactory(c -> {
            User r = c.getValue().getRequestor();
            return new ReadOnlyStringWrapper(r != null ? r.getName() : "");
        });
        commentsColumn.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getComments()));
    }

    private void setupEquipmentColumns() {
        eqPartColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getPartNumber()));
        eqDescColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getDescription()));
        eqSerialColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getSerialNumber()));
        eqConditionColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemCondition().name()));
        eqStatusColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemStatus().name()));
        eqJobOrderColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getJobOrderId() != null
                                ? c.getValue().getJobOrderId().toString() : ""));
    }

    private void setupActions() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(WorkOrderStatus.values()));
        refreshButton.setOnAction(e -> refreshTable());
        createButton.setOnAction(e -> handleNewWorkOrder());
        updateButton.setOnAction(e -> updateWorkOrder());
        deleteButton.setOnAction(e -> deleteWorkOrder());
        clearButton.setOnAction(e -> clearForm());
        workOrderTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldV, newV) -> showDetails(newV));
    }

    private void setupContextMenu() {
        workOrderTable.setRowFactory(tv -> {
            TableRow<WorkOrder> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem ni = new MenuItem("New");
            ni.setOnAction(e -> clearForm());
            MenuItem ed = new MenuItem("Edit");
            ed.setOnAction(e -> showDetails(row.getItem()));
            MenuItem del = new MenuItem("Delete");
            del.setOnAction(e -> {
                workOrderService.delete(row.getItem().getId());
                refreshTable();
                clearForm();
            });
            menu.getItems().addAll(ni, ed, del);
            row.setOnContextMenuRequested((ContextMenuEvent e) -> {
                if (!row.isEmpty()) menu.show(row, e.getScreenX(), e.getScreenY());
            });
            return row;
        });
    }

    public void refreshTable() {
        List<WorkOrder> data = statusFilterCombo.getValue() != null
                ? workOrderService.getByStatus(statusFilterCombo.getValue())
                : workOrderService.getAll();
        workOrderTable.setItems(FXCollections.observableArrayList(data));
    }

    private void handleNewWorkOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/new_workorder.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent form = loader.load();
            Stage dlg = new Stage();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("New Work Order");
            dlg.getIcons().add(new Image(
                    getClass().getResourceAsStream("/icons/new_workorder.png")
            ));
            dlg.setScene(new Scene(form));
            dlg.showAndWait();
            refreshTable();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateWorkOrder() {
        WorkOrder sel = workOrderTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert("No selection", "Select a work order to update.");
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
            alert("No selection", "Select a work order to delete.");
            return;
        }
        for (Long id : sel.getItemsId()) {
            if (itemService.getById(id).getJobOrderId() != null) {
                alert("Job Order presence", "Selected WorkOrder have associated Job Orders.");
                return;
            }
        }
        workOrderService.delete(sel.getId());
        refreshTable();
        clearForm();
    }

    private void clearForm() {
        showDetails(null);
        numberField.clear();
        wellField.clear();
        deliveryDatePicker.setValue(null);
        commentsArea.clear();
    }

    private void showDetails(WorkOrder order) {
        if (order == null) {
            detailNumberLabel.setText("");
            detailClientLabel.setText("");
            detailWellLabel.setText("");
            detailRequestDateLabel.setText("");
            detailDeliveryDateLabel.setText("");
            detailStatusLabel.setText("");
            detailRequestorLabel.setText("");
            detailCommentsArea.clear();
            equipmentTable.getItems().clear();
        } else {
            detailNumberLabel.setText(order.getWorkOrderNumber());
            detailClientLabel.setText(order.getClient().name());
            detailWellLabel.setText(order.getWell());
            detailRequestDateLabel.setText(order.getRequestDate().toString());
            detailDeliveryDateLabel.setText(order.getDeliveryDate().toString());
            detailStatusLabel.setText(order.getStatus().name());
            detailRequestorLabel.setText(order.getRequestor() != null ? order.getRequestor().getName() : "");
            detailCommentsArea.setText(order.getComments());
            List<Item> items = order.getItemsId().stream().map(itemService::getById).collect(Collectors.toList());
            equipmentTable.setItems(FXCollections.observableArrayList(items));
        }
    }

    private void alert(String h, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(h);
        a.setContentText(c);
        a.showAndWait();
    }
}
