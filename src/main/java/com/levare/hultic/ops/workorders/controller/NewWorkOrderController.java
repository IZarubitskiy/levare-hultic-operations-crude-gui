package com.levare.hultic.ops.workorders.controller;

import com.levare.hultic.ops.iteminfos.controller.ItemInfoSelectionController;
import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.items.entity.Item;
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
    @FXML private TextField wellField;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private TextArea commentsArea;

    @FXML private TableView<Item> itemsTable;
    @FXML private TableColumn<Item, String> partNumberColumn;
    @FXML private TableColumn<Item, String> descriptionColumn;
    @FXML private TableColumn<Item, String> serialNumberColumn;
    @FXML private TableColumn<Item, String> ownershipColumn;
    @FXML private TableColumn<Item, String> conditionColumn;
    @FXML private TableColumn<Item, String> jobOrderColumn;
    @FXML private TableColumn<Item, String> commentsColumn;

    @FXML private Button newItemButton;
    @FXML private Button cancelButton;

    @FXML
    private void initialize() {
        // Настраиваем колонки таблицы
        partNumberColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getItemInfo().getPartNumber()));
        descriptionColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getItemInfo().getDescription()));
        serialNumberColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getSerialNumber()));
        ownershipColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getOwnership().toString()));
        conditionColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getItemCondition().toString()));
        jobOrderColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getJobOrder().getId().toString()));
        commentsColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getComments()));

        // Привязываем список к таблице
        itemsTable.setItems(selectedItems);
    }

    /** Открывает модальное окно выбора оборудования и добавляет выбранный ItemInfo как новый Item */
    @FXML
    private void onNewItem() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/item_info_selection.fxml")
            );
            // Инжектим ItemInfoService в контроллер выбора
            loader.setControllerFactory(ctrlClass -> {
                if (ctrlClass == ItemInfoSelectionController.class) {
                    return new ItemInfoSelectionController(itemInfoService);
                }
                try {
                    return ctrlClass.getDeclaredConstructor().newInstance();
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

            ItemInfoSelectionController selectionCtrl = loader.getController();
            ItemInfo chosen = selectionCtrl.getSelectedItem();
            if (chosen != null) {
                Item item = new Item();
                item.setItemInfo(chosen);
                selectedItems.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Не удалось открыть диалог выбора оборудования", e.getMessage());
        }
    }

    /** Сохраняет новую заявку вместе с выбранным оборудованием */
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

        // TODO: добавить selectedItems в order (например, order.setItems(List.copyOf(selectedItems)));

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
