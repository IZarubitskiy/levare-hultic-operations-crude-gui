package com.levare.hultic.ops.items.controller;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.workorders.entity.Client;
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
    private final Client client;
    private Item selectedItem;

    public FilteredItemSelectionController(ItemService itemService, Mode mode, Client client) {
        this.itemService = itemService;
        this.mode        = mode;
        this.client      = client;
    }

    @FXML private Label titleLabel;
    @FXML private TableView<Item> tableView;
    @FXML private TableColumn<Item,String> partColumn;
    @FXML private TableColumn<Item,String> descColumn;
    @FXML private TableColumn<Item, String> serialColumn;
    @FXML private TableColumn<Item,String> statusColumn;
    @FXML private Button selectButton;

    @FXML
    private void initialize() {
        // dialog title includes the client name
        titleLabel.setText("Select "
                + mode.name().toLowerCase()
                + " items for client "
                + client.name());

        // configure columns
        partColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getPartNumber()));
        descColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getDescription()));
        serialColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getSerialNumber()));
        statusColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(
                        c.getValue().getItemStatus() != null
                                ? c.getValue().getItemStatus().name()
                                : ""
                )
        );

        // load all items and filter by both mode and selected client
        List<Item> filtered = itemService.getAll().stream()
                .filter(this::matchesModeAndClient)
                .collect(Collectors.toList());

        tableView.setItems(FXCollections.observableArrayList(filtered));

        // disable Select button until a row is chosen
        selectButton.disableProperty()
                .bind(tableView.getSelectionModel().selectedItemProperty().isNull());
    }

    private boolean matchesModeAndClient(Item item) {
        // first: must belong to the chosen client
        if (item.getOwnership() != client) {
            return false;
        }

        // then filter by the chosen mode
        switch (mode) {
            case REPAIR:
                                return item.getItemCondition() == ItemCondition.USED
                        && item.getItemStatus()    == ItemStatus.ON_STOCK;
            case STOCK:
                return (item.getItemCondition() == ItemCondition.REPAIRED
                        || item.getItemCondition() == ItemCondition.NEW)
                        && item.getItemStatus()     == ItemStatus.ON_STOCK;
            case RNE:
                return item.getItemCondition() == ItemCondition.USED
                        && item.getItemStatus() == ItemStatus.ON_STOCK;
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
