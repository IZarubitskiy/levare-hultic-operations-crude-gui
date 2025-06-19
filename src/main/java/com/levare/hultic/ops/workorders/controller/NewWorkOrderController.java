package com.levare.hultic.ops.workorders.controller;

import com.levare.hultic.ops.iteminfos.controller.ItemInfoSelectionController;
import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.workorders.entity.Client;
import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import com.levare.hultic.ops.users.entity.User;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class NewWorkOrderController {

    private final WorkOrderService workOrderService;
    private final com.levare.hultic.ops.iteminfos.service.ItemInfoService itemInfoService;
    private final ObservableList<Item> selectedItems = FXCollections.observableArrayList();

    @FXML private TextField numberField;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private TextField wellField;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private TextField requestorField;
    @FXML private TextArea commentsArea;

    @FXML private TableView<Item> itemsTable;
    @FXML private TableColumn<Item, String> partNumberColumn;
    @FXML private TableColumn<Item, String> descriptionColumn;
    @FXML private TableColumn<Item, String> serialNumberColumn;
    @FXML private TableColumn<Item, String> ownershipColumn;
    @FXML private TableColumn<Item, String> conditionColumn;
    @FXML private TableColumn<Item, String> statusColumn;
    @FXML private TableColumn<Item, String> jobOrderColumn;
    @FXML private TableColumn<Item, String> commentsColumn;

    @FXML private Button newItemButton;
    @FXML private Button deleteItemButton;
    @FXML private Button cancelButton;

    @FXML
    private void initialize() {
        // Populate client dropdown
        clientComboBox.setItems(FXCollections.observableArrayList(Client.values()));
        clientComboBox.getSelectionModel().select(Client.EMPTY);

        // Configure table columns
        partNumberColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getPartNumber()));
        descriptionColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getDescription()));
        serialNumberColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getSerialNumber()));
        ownershipColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getOwnership() != null
                                ? c.getValue().getOwnership().name()
                                : ""));
        conditionColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemCondition().name()));
        statusColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getItemStatus() != null
                                ? c.getValue().getItemStatus().name()
                                : ""));
        jobOrderColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getJobOrder() != null
                                ? c.getValue().getJobOrder().getId().toString()
                                : ""));
        commentsColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getComments()));

        // Set up table view
        itemsTable.setItems(selectedItems);
        itemsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Disable client selection once at least one item is added
        selectedItems.addListener((ListChangeListener<Item>) ch ->
                clientComboBox.setDisable(!selectedItems.isEmpty())
        );

        // Enable delete button only when an item is selected
        deleteItemButton.disableProperty()
                .bind(itemsTable.getSelectionModel().selectedItemProperty().isNull());
    }

    /** Opens item selection dialog and creates a new Item with defaults */
    @FXML
    private void onNewItem() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/item_info_selection.fxml")
            );
            loader.setControllerFactory(cls -> {
                if (cls == ItemInfoSelectionController.class) {
                    return new ItemInfoSelectionController(itemInfoService);
                }
                try {
                    return cls.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Select Equipment");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            ItemInfoSelectionController ctrl = loader.getController();
            ItemInfo info = ctrl.getSelectedItem();
            if (info != null) {
                Item item = new Item();
                item.setItemInfo(info);
                item.setClientPartNumber(info.getPartNumber());
                item.setSerialNumber("TBA");
                item.setOwnership(clientComboBox.getValue());
                item.setItemCondition(ItemCondition.NEW_ASSEMBLY);
                item.setItemStatus(ItemStatus.NEW_ASSEMBLY_REQUEST);
                item.setJobOrder(null);
                item.setComments("");
                selectedItems.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to open equipment selection dialog:\n" + e.getMessage());
        }
    }

    /** Deletes the selected item from the list */
    @FXML
    private void onDeleteItem() {
        Item sel = itemsTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            selectedItems.remove(sel);
        }
    }

    /** Saves new WorkOrder along with the prepared Items */
    @FXML
    private void handleSave() {
        if (workOrderService == null) {
            showError("Configuration Error", "WorkOrderService is not set.");
            return;
        }

        WorkOrder order = new WorkOrder();
        order.setWorkOrderNumber(numberField.getText().trim());
        order.setClient(clientComboBox.getValue());
        order.setWell(wellField.getText().trim());
        order.setDeliveryDate(deliveryDatePicker.getValue());
        order.setRequestor(new User(null, requestorField.getText().trim(), null));
        order.setComments(commentsArea.getText().trim());
        order.setRequestDate(LocalDate.now());
        order.setStatus(WorkOrderStatus.CREATED);

        selectedItems.forEach(order::addItem);

        workOrderService.create(order);
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) numberField.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
