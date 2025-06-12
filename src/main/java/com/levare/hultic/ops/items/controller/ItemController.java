package com.levare.hultic.ops.items.controller;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.workorders.entity.Client;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JavaFX controller for interacting with the item list and filter UI.
 */
public class ItemController {

    private ItemService itemService;

    @FXML
    private TableView<Item> itemTable;

    @FXML
    private TableColumn<Item, String> serialNumberColumn;

    @FXML
    private TableColumn<Item, ItemCondition> conditionColumn;

    @FXML
    private TableColumn<Item, ItemStatus> statusColumn;

    @FXML
    private TableColumn<Item, Client> ownershipColumn;

    @FXML
    private ComboBox<Client> filterOwnership;

    @FXML
    private Button refreshButton;

    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    @FXML
    private void initialize() {
        serialNumberColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSerialNumber()));
        conditionColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getItemCondition()));
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getItemStatus()));
        ownershipColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getOwnership()));

        filterOwnership.setItems(FXCollections.observableArrayList(Client.values()));
        refreshButton.setOnAction(e -> refreshTable());
    }

    private void refreshTable() {
        List<ItemCondition> conditions = List.of(ItemCondition.NEW, ItemCondition.REPAIRED, ItemCondition.USED);
        List<ItemStatus> statuses = List.of(ItemStatus.ON_STOCK, ItemStatus.STOCK_BOOKED, ItemStatus.REPAIR_REQUEST, ItemStatus.REPAIR_BOOKED);
        Client selectedOwner = filterOwnership.getValue();

        List<Item> items;
        if (selectedOwner == null) {
            items = itemService.getAll();
        } else {
            items = itemService.getAll().stream()
                    .filter(i -> i.getOwnership() == selectedOwner)
                    .filter(i -> conditions.contains(i.getItemCondition()))
                    .filter(i -> statuses.contains(i.getItemStatus()))
                    .collect(Collectors.toList());
        }

        itemTable.setItems(FXCollections.observableArrayList(items));
    }
}
