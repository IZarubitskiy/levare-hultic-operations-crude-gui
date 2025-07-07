package com.levare.hultic.ops.workorders.controller;

import com.levare.hultic.ops.iteminfos.controller.ItemInfoSelectionController;
import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.items.controller.FilteredItemSelectionController;
import com.levare.hultic.ops.items.controller.FilteredItemSelectionController.Mode;
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
    private final com.levare.hultic.ops.items.service.ItemService itemService;

    private final ObservableList<Item> selectedItems = FXCollections.observableArrayList();

    @FXML private TextField numberField;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private TextField wellField;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private TextField requestorField;
    @FXML private TextArea commentsField;

    @FXML private TableView<Item> itemsTable;
    @FXML private TableColumn<Item,String> partNumberColumn;
    @FXML private TableColumn<Item,String> descriptionColumn;
    @FXML private TableColumn<Item,String> serialNumberColumn;
    @FXML private TableColumn<Item,String> ownershipColumn;
    @FXML private TableColumn<Item,String> conditionColumn;
    @FXML private TableColumn<Item,String> statusColumn;
    @FXML private TableColumn<Item,String> jobOrderColumn;
    @FXML private TableColumn<Item,String> commentsColumn;

    @FXML private Button newItemButton;
    @FXML private Button deleteItemButton;
    @FXML private Button repairButton;
    @FXML private Button stockButton;
    @FXML private Button rneButton;
    @FXML private Button cancelButton;

    @FXML
    private void initialize() {
        // заполняем Combobox' клиентами
        clientComboBox.setItems(FXCollections.observableArrayList(Client.values()));
        clientComboBox.getSelectionModel().select(Client.EMPTY);

        // настраиваем таблицу
        partNumberColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getPartNumber()));
        descriptionColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getDescription()));
        serialNumberColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getSerialNumber()));
        ownershipColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getOwnership() != null ? c.getValue().getOwnership().name() : ""));
        conditionColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemCondition().name()));
        statusColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getItemStatus() != null ? c.getValue().getItemStatus().name() : ""));
        jobOrderColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getJobOrder() != null ? c.getValue().getJobOrder().getId().toString() : ""));
        commentsColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getComments()));

        itemsTable.setItems(selectedItems);
        itemsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // запретить менять клиента, когда добавлен первый item
        selectedItems.addListener((ListChangeListener<Item>) ch ->
                clientComboBox.setDisable(!selectedItems.isEmpty())
        );

        // кнопка удаления активна только при выборе строки
        deleteItemButton.disableProperty()
                .bind(itemsTable.getSelectionModel().selectedItemProperty().isNull());

        // настройки фильтрующих кнопок
        repairButton.setOnAction(e -> openFilteredDialog(Mode.REPAIR));
        stockButton .setOnAction(e -> openFilteredDialog(Mode.STOCK));
        rneButton   .setOnAction(e -> openFilteredDialog(Mode.RNE));
    }

    /** Открыть диалог выбора из общего каталога item_info */
    @FXML
    private void onNewItem() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/item_info_selection.fxml"));
            loader.setControllerFactory(cls -> {
                if (cls == ItemInfoSelectionController.class) {
                    return new ItemInfoSelectionController(itemInfoService);
                }
                try { return cls.getDeclaredConstructor().newInstance(); }
                catch (Exception ex) { throw new RuntimeException(ex); }
            });

            Parent root = loader.load();
            Stage dlg = new Stage();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Select Equipment");
            dlg.setScene(new Scene(root));
            dlg.showAndWait();

            var ctrl = loader.<ItemInfoSelectionController>getController();
            ItemInfo info = ctrl.getSelectedItem();
            if (info != null) {
                Item item = new Item();
                item.setItemInfo(info);
                item.setClientPartNumber(info.getPartNumber());
                item.setSerialNumber("TBA");
                item.setOwnership(clientComboBox.getValue());
                item.setItemCondition(ItemCondition.NEW_ASSEMBLY);
                item.setItemStatus(ItemStatus.NEW_ASSEMBLY_BOOKED);
                item.setJobOrder(null);
                item.setComments("");
                selectedItems.add(item);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Ошибка", "Не удалось открыть диалог:\n" + ex.getMessage());
        }
    }

    /** Открыть общий фильтруемый диалог для REPAIR/STOCK/RNE */
    private void openFilteredDialog(Mode mode) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/filtered_item_selection.fxml"));
            loader.setControllerFactory(cls -> {
                if (cls == FilteredItemSelectionController.class) {
                    return new FilteredItemSelectionController(
                            itemService, mode, clientComboBox.getValue());
                }
                try { return cls.getDeclaredConstructor().newInstance(); }
                catch (Exception ex) { throw new RuntimeException(ex); }
            });

            Parent root = loader.load();
            Stage dlg = new Stage();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Select " + mode.name());
            dlg.setScene(new Scene(root));
            dlg.showAndWait();

            var ctrl = loader.<FilteredItemSelectionController>getController();
            Item chosen = ctrl.getSelectedItem();
            if (chosen != null) {
                selectedItems.add(chosen);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Ошибка", "Не удалось открыть фильтрующий диалог:\n" + ex.getMessage());
        }
    }

    /** Удалить выбранный элемент */
    @FXML
    private void onDeleteItem() {
        Item sel = itemsTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            selectedItems.remove(sel);
        }
    }

    /** Сохранить заявку вместе с подготовленными элементами */
    @FXML
    private void handleSave() {
        if (workOrderService == null) {
            showError("Ошибка конфигурации", "WorkOrderService не задан.");
            return;
        }

        WorkOrder order = new WorkOrder();
        order.setWorkOrderNumber(numberField.getText().trim());
        order.setClient(clientComboBox.getValue());
        order.setWell(wellField.getText().trim());
        order.setDeliveryDate(deliveryDatePicker.getValue());
        order.setRequestor(new User(null, requestorField.getText().trim(), null));
        order.setComments(commentsField.getText().trim());
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

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
