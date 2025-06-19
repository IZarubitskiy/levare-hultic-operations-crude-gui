package com.levare.hultic.ops.items.controller;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.items.service.ItemService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class FilteredItemSelectionController {

    public enum Mode { REPAIR, STOCK, RNE }

    private final ItemService itemService;
    private final Mode mode;
    private Item selectedItem;

    @FXML private Label titleLabel;
    @FXML private TableView<Item> tableView;
    @FXML private TableColumn<Item,String> partColumn;
    @FXML private TableColumn<Item,String> descColumn;
    @FXML private TableColumn<Item,String> statusColumn;
    @FXML private Button selectButton;

    public FilteredItemSelectionController(ItemService itemService, Mode mode) {
        this.itemService = itemService;
        this.mode = mode;
    }

    @FXML
    private void initialize() {
        // заголовок диалога
        titleLabel.setText("Select " + mode.name().toLowerCase() + " items");

        // настройка колонок
        partColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getPartNumber()));
        descColumn .setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getDescription()));
        statusColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getItemStatus() != null
                                ? c.getValue().getItemStatus().name() : ""));

        // подгружаем все и фильтруем
        List<Item> all = itemService.getAll();
        List<Item> filtered = all.stream()
                .filter(this::matchesMode)
                .collect(Collectors.toList());

        tableView.setItems(FXCollections.observableArrayList(filtered));

        // кнопка Select
        selectButton.disableProperty()
                .bind(tableView.getSelectionModel().selectedItemProperty().isNull());
    }

    private boolean matchesMode(Item item) {
        switch (mode) {
            case REPAIR:
                return item.getItemCondition() == ItemCondition.USED;
            case STOCK:
                return item.getItemStatus() == ItemStatus.IN_STOCK;
            case RNE:
                return item.getItemStatus() == ItemStatus.READY_FOR_USE;
            default:
                return false;
        }
    }

    @FXML
    private void onSelect() {
        selectedItem = tableView.getSelectionModel().getSelectedItem();
        close();
    }

    @FXML
    private void onCancel() {
        selectedItem = null;
        close();
    }

    private void close() {
        ((Stage)tableView.getScene().getWindow()).close();
    }

    public Item getSelectedItem() {
        return selectedItem;
    }
}
