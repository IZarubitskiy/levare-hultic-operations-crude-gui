package com.levare.hultic.ops.workorders.controller;

import com.levare.hultic.ops.iteminfos.controller.ItemInfoSelectionController;
import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import com.levare.hultic.ops.iteminfos.service.ItemInfoServiceImpl;
import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class NewWorkOrderController {

    @FXML private TextField numberField;
    @FXML private TextField wellField;
    @FXML private DatePicker deliveryDatePicker;
    @FXML private TextArea commentsArea;
    @FXML private Button cancelButton;

    // Table for selected equipment
    @FXML private TableView<ItemInfo> itemsTable;
    @FXML private TableColumn<ItemInfo, String> partNumberColumn;
    @FXML private TableColumn<ItemInfo, String> descriptionColumn;
    @FXML private Button newItemButton;

    private WorkOrderService workOrderService;
    private final ItemInfoService itemInfoService = new ItemInfoServiceImpl();
    private final ObservableList<ItemInfo> selectedItems = FXCollections.observableArrayList();

    public void setWorkOrderService(WorkOrderService service) {
        this.workOrderService = service;
    }

    @FXML
    public void initialize() {
        // Настроим колонки таблицы выбранных ItemInfo
        partNumberColumn.setCellValueFactory(cell -> cell.getValue().getPartNumber());
        descriptionColumn.setCellValueFactory(cell -> cell.getValue().getDescription());
        itemsTable.setItems(selectedItems);
    }

    /** Открывает модальное окно выбора оборудования и добавляет выбранное в таблицу */
    @FXML
    private void onNewItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/item_info_selection.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Выберите оборудование");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            ItemInfoSelectionController controller = loader.getController();
            ItemInfo chosen = controller.getSelectedItem();
            if (chosen != null) {
                selectedItems.add(chosen);
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
            System.err.println("❌ WorkOrderService is not set.");
            return;
        }

        WorkOrder order = new WorkOrder();
        order.setWorkOrderNumber(numberField.getText().trim());
        order.setWell(wellField.getText().trim());
        order.setDeliveryDate(deliveryDatePicker.getValue());
        order.setComments(commentsArea.getText().trim());
        order.setRequestDate(LocalDate.now());
        order.setStatus(WorkOrderStatus.CREATED);

        // Предполагается, что WorkOrder имеет метод setItems(List<ItemInfo>)
        order.setItems(List.copyOf(selectedItems));

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
