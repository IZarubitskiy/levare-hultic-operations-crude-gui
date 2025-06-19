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
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
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
    @FXML private Button cancelButton;

    @FXML
    private void initialize() {
        clientComboBox.setItems(FXCollections.observableArrayList(Client.values()));
        clientComboBox.getSelectionModel().select(Client.EMPTY);
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
        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().getItemStatus() != null
                                ? cell.getValue().getItemStatus().name()
                                : ""
                )
        );
        jobOrderColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getJobOrder() != null
                                ? c.getValue().getJobOrder().getId().toString()
                                : ""));
        commentsColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getComments()));

        itemsTable.setItems(selectedItems);
        itemsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        itemsTable.setItems(selectedItems);
    }

    /** Открывает диалог выбора ItemInfo и создаёт новый Item с дефолтами */
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
            dialog.setTitle("Выберите оборудование");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            ItemInfoSelectionController ctrl = loader.getController();
            ItemInfo info = ctrl.getSelectedItem();
            if (info != null) {
                Item item = new Item();
                item.setItemInfo(info);
                item.setClientPartNumber(info.getPartNumber());
                item.setSerialNumber("TBA");

                // ← вот здесь берём текущего клиента из выпадающего списка
                Client selectedClient = clientComboBox.getValue();
                item.setOwnership(selectedClient);

                item.setItemCondition(ItemCondition.NEW_ASSEMBLY);

                // ← устанавливаем статус именно NEW_ASSEMBLY_REQUEST
                item.setItemStatus(ItemStatus.NEW_ASSEMBLY_REQUEST);

                item.setJobOrder(null);
                item.setComments("");

                selectedItems.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Не удалось открыть диалог выбора оборудования", e.getMessage());
        }
    }

    /** Сохраняет новую заявку вместе со всеми подготовленными Item */
    @FXML
    private void handleSave() {
        if (workOrderService == null) {
            showError("Ошибка конфигурации", "WorkOrderService не задан.");
            return;
        }

        WorkOrder order = new WorkOrder();
        order.setWorkOrderNumber(numberField.getText().trim());
        order.setWell(wellField.getText().trim());
        order.setDeliveryDate(deliveryDatePicker.getValue());
        order.setComments(commentsArea.getText().trim());
        order.setRequestDate(LocalDate.now());
        order.setStatus(WorkOrderStatus.CREATED);

        // Добавляем все выбранные Items в WorkOrder
        for (Item item : selectedItems) {
            order.addItem(item);
        }

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

    private void showError(String header, String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка");
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}
