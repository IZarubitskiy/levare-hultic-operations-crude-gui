package com.levare.hultic.ops.iteminfos.controller;

import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.entity.ItemType;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * JavaFX controller for managing ItemInfo form and table.
 */
public class ItemInfoController {

    private ItemInfoService service;

    @FXML
    private TextField partNumberField;

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<ItemType> itemTypeComboBox;

    @FXML
    private TextArea commentsField;

    @FXML
    private TableView<ItemInfo> itemTable;

    @FXML
    private TableColumn<ItemInfo, String> partNumberColumn;

    @FXML
    private TableColumn<ItemInfo, String> descriptionColumn;

    @FXML
    private TableColumn<ItemInfo, ItemType> itemTypeColumn;

    public void setService(ItemInfoService service) {
        this.service = service;
    }

    @FXML
    private void initialize() {
        // Configure table columns
        partNumberColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPartNumber()));
        descriptionColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        itemTypeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getItemType()));

        // Load item types into ComboBox
        itemTypeComboBox.setItems(FXCollections.observableArrayList(ItemType.values()));

        // Load data
        refreshTable();
    }

    @FXML
    private void handleCreate() {
        ItemInfo info = new ItemInfo();
        info.setPartNumber(partNumberField.getText());
        info.setDescription(descriptionField.getText());
        info.setItemType(itemTypeComboBox.getValue());
        info.setComments(commentsField.getText());

        try {
            service.create(info);
            refreshTable();
            clearForm();
        } catch (IllegalArgumentException e) {
            showAlert("Validation error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        ItemInfo selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection error", "Please select an item to update.");
            return;
        }

        selected.setDescription(descriptionField.getText());
        selected.setItemType(itemTypeComboBox.getValue());
        selected.setComments(commentsField.getText());

        try {
            service.update(selected);
            refreshTable();
            clearForm();
        } catch (IllegalArgumentException e) {
            showAlert("Update error", e.getMessage());
        }
    }

    @FXML
    private void handleRowSelect() {
        ItemInfo selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            partNumberField.setText(selected.getPartNumber());
            descriptionField.setText(selected.getDescription());
            itemTypeComboBox.setValue(selected.getItemType());
            commentsField.setText(selected.getComments());
        }
    }

    private void refreshTable() {
        List<ItemInfo> allItems = service.getAll();
        itemTable.setItems(FXCollections.observableArrayList(allItems));
    }

    private void clearForm() {
        partNumberField.clear();
        descriptionField.clear();
        itemTypeComboBox.setValue(null);
        commentsField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
