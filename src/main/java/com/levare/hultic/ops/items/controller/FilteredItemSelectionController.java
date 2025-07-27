package com.levare.hultic.ops.items.controller;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.workorders.entity.Client;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class FilteredItemSelectionController {

    private final ItemService itemService;
    private final List<Client> clients;
    private final List<ItemCondition> conditions;
    private final List<ItemStatus> statuses;

    private Item selectedItem;

    /**
     * @param itemService сервис доступа к Item
     * @param clients     список клиентов (если пустой — без фильтра по клиенту)
     * @param conditions  список состояний (если пустой — без фильтра по condition)
     * @param statuses    список статусов (если пустой — без фильтра по status)
     */
    public FilteredItemSelectionController(ItemService itemService,
                                           List<Client> clients,
                                           List<ItemCondition> conditions,
                                           List<ItemStatus> statuses) {
        this.itemService = itemService;
        this.clients = clients;
        this.conditions = conditions;
        this.statuses = statuses;
    }

    @FXML
    private Label titleLabel;
    @FXML
    private TableView<Item> tableView;
    @FXML
    private TableColumn<Item, String> partColumn, descColumn, serialColumn, statusColumn, conditionColumn;
    @FXML
    private Button selectButton, cancelButton;

    @FXML
    private void initialize() {
        // Заголовок с перечислением фильтров
        String clientsStr = clients.isEmpty() ? "all" : clients.stream().map(Client::name).collect(Collectors.joining(", "));
        String condsStr = conditions.isEmpty() ? "all" : conditions.stream().map(Enum::name).collect(Collectors.joining(", "));
        String statusesStr = statuses.isEmpty() ? "all" : statuses.stream().map(Enum::name).collect(Collectors.joining(", "));
        titleLabel.setText(String.format("Items — clients: [%s], conditions: [%s], statuses: [%s]",
                clientsStr, condsStr, statusesStr));

        // колонки
        partColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getPartNumber()));
        descColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemInfo().getDescription()));
        serialColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getSerialNumber()));
        statusColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemStatus() != null
                        ? c.getValue().getItemStatus().name() : ""));
        conditionColumn.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getItemCondition() != null
                        ? c.getValue().getItemCondition().name() : ""));


        // загрузка и фильтрация
        List<Item> filtered = itemService.getAll().stream()
                .filter(this::matchesAll)
                .collect(Collectors.toList());
        tableView.setItems(FXCollections.observableArrayList(filtered));

        // кнопка ОК
        selectButton.disableProperty()
                .bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        selectButton.setOnAction(e -> {
            selectedItem = tableView.getSelectionModel().getSelectedItem();
            close();
        });
        cancelButton.setOnAction(e -> {
            selectedItem = null;
            close();
        });
    }

    private boolean matchesAll(Item item) {
        boolean okClient = clients.isEmpty() || clients.contains(item.getOwnership());
        boolean okCondition = conditions.isEmpty() || conditions.contains(item.getItemCondition());
        boolean okStatus = statuses.isEmpty() || statuses.contains(item.getItemStatus());
        return okClient && okCondition && okStatus;
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
        ((Stage) tableView.getScene().getWindow()).close();
    }

    public Item getSelectedItem() {
        return selectedItem;
    }
}