package com.levare.hultic.ops.workorders.controller;

import com.levare.hultic.ops.iteminfos.controller.ItemInfoSelectionController;
import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import com.levare.hultic.ops.items.controller.FilteredItemSelectionController;
import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.users.service.UserService;
import com.levare.hultic.ops.workorders.entity.Client;
import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
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
import java.util.List;

@RequiredArgsConstructor
public class NewWorkOrderController {

    private final WorkOrderService    workOrderService;
    private final ItemInfoService     itemInfoService;
    private final ItemService         itemService;
    private final UserService         userService;

    private final ObservableList<Item> selectedItems = FXCollections.observableArrayList();

    @FXML private TextField       numberField;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private TextField       wellField;
    @FXML private DatePicker      deliveryDatePicker;

    @FXML private ComboBox<User>  requestorComboBox;
    @FXML private TextArea        commentsField;

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
    @FXML private Button wipButton;     // ← новая кнопка WIP

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML
    private void initialize() {
        // Клиенты
        clientComboBox.setItems(FXCollections.observableArrayList(Client.values()));
        clientComboBox.getSelectionModel().select(Client.EMPTY);

        // Реквесторы
        requestorComboBox.setItems(FXCollections.observableArrayList(userService.getAll()));
        requestorComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(User u, boolean empty){
                super.updateItem(u,empty);
                setText(empty||u==null?null:u.getName());
            }
        });
        requestorComboBox.setButtonCell(requestorComboBox.getCellFactory().call(null));

        // Таблица items
        partNumberColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getPartNumber()));
        descriptionColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getDescription()));
        serialNumberColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getSerialNumber()));
        ownershipColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getOwnership()!=null ? c.getValue().getOwnership().name() : ""));
        conditionColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemCondition().name()));
        statusColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getItemStatus()!=null ? c.getValue().getItemStatus().name() : ""));
        jobOrderColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getJobOrderId()!=null ? c.getValue().getJobOrderId().toString() : ""));
        commentsColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getComments()));

        itemsTable.setItems(selectedItems);
        itemsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Блокировка смены клиента при наличии строк
        selectedItems.addListener((ListChangeListener<Item>) ch ->
                clientComboBox.setDisable(!selectedItems.isEmpty())
        );

        // Удаление
        deleteItemButton.disableProperty()
                .bind(itemsTable.getSelectionModel().selectedItemProperty().isNull());

        // Фильтрующие кнопки
        repairButton.setOnAction(e -> openFilteredDialog(
                List.of(clientComboBox.getValue()),
                List.of(ItemCondition.USED),
                List.of(ItemStatus.ON_STOCK)
        ));
        stockButton.setOnAction(e -> openFilteredDialog(
                List.of(clientComboBox.getValue()),
                List.of(ItemCondition.NEW, ItemCondition.REPAIRED),
                List.of(ItemStatus.ON_STOCK)
        ));
        rneButton.setOnAction(e -> openFilteredDialog(
                List.of(Client.RNE, Client.CORPORATE),
                List.of(ItemCondition.USED),
                List.of(ItemStatus.ON_STOCK)
        ));
        wipButton.setOnAction(e -> onWip());  // обработчик новой кнопки

        // Новый из каталога
        newItemButton.setOnAction(e -> onNewItem());

        // Сохранить/отмена
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());
    }

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
                try { return cls.getDeclaredConstructor().newInstance(); }
                catch (Exception ex) { throw new RuntimeException(ex); }
            });
            Parent root = loader.load();
            Stage dlg = new Stage(); dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Select Equipment");
            dlg.setScene(new Scene(root));
            dlg.showAndWait();

            ItemInfo info = loader.<ItemInfoSelectionController>getController().getSelectedItem();
            if (info != null) {
                Item it = new Item();
                it.setItemInfo(info);
                it.setOwnership(clientComboBox.getValue());
                it.setSerialNumber("TBA");
                it.setItemCondition(ItemCondition.NEW_ASSEMBLY);
                it.setItemStatus(ItemStatus.NEW_ASSEMBLY_BOOKED);
                it.setJobOrderId(null);
                it.setComments("");
                selectedItems.add(it);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error", "Cannot open selection dialog:\n"+ex.getMessage());
        }
    }

    private void openFilteredDialog(List<Client> clients,
                                    List<ItemCondition> conds,
                                    List<ItemStatus> stats) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/filtered_item_selection.fxml")
            );
            loader.setControllerFactory(cls -> {
                if (cls == FilteredItemSelectionController.class) {
                    return new FilteredItemSelectionController(itemService, clients, conds, stats);
                }
                try { return cls.getDeclaredConstructor().newInstance(); }
                catch (Exception ex) { throw new RuntimeException(ex); }
            });
            Parent root = loader.load();
            Stage dlg = new Stage(); dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Select Items");
            dlg.setScene(new Scene(root));
            dlg.showAndWait();

            Item chosen = loader.<FilteredItemSelectionController>getController().getSelectedItem();
            if (chosen != null) selectedItems.add(chosen);

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error", "Cannot open filtered dialog:\n"+ex.getMessage());
        }
    }

    @FXML
    private void onDeleteItem() {
        Item sel = itemsTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            selectedItems.remove(sel);
        }
    }

    /** Обработчик WIP-кнопки */
    @FXML
    private void onWip() {
        // TODO: здесь ваша логика пометки выбранных позиций как WIP

    }

    @FXML
    private void handleSave() {
        User req = requestorComboBox.getValue();
        if (req == null) {
            showError("Validation Error","Select a requestor.");
            return;
        }
        WorkOrder newWorkOrder = new WorkOrder();
        newWorkOrder.setWorkOrderNumber(numberField.getText().trim());
        newWorkOrder.setClient(clientComboBox.getValue());
        newWorkOrder.setWell(wellField.getText().trim());
        newWorkOrder.setDeliveryDate(deliveryDatePicker.getValue());
        newWorkOrder.setRequestDate(LocalDate.now());
        newWorkOrder.setRequestor(req);
        newWorkOrder.setComments(commentsField.getText().trim());
        newWorkOrder.setStatus(WorkOrderStatus.CREATED);
        selectedItems.forEach(it -> {
            if(it.getId() == null && it.getItemCondition() == ItemCondition.NEW_ASSEMBLY) {
                newWorkOrder.addItemId(itemService.newItemFromCatalog(it.getItemInfo().getPartNumber(),clientComboBox.getValue()).getId());
            } else {
                newWorkOrder.addItemId(it.getId());
            }
        });
        workOrderService.create(newWorkOrder);
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage st = (Stage) numberField.getScene().getWindow();
        st.close();
    }

    private void showError(String hdr, String txt) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(hdr);
        a.setContentText(txt);
        a.showAndWait();
    }
}
