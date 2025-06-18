package com.levare.hultic.ops.iteminfos.controller;

import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

/**
 * Controller for the modal dialog to select an ItemInfo.
 * Получает ItemInfoService через конструктор от AppControllerFactory.
 */
@RequiredArgsConstructor
public class ItemInfoSelectionController {

    private final ItemInfoService itemInfoService;
    private ItemInfo selectedItem;

    @FXML private TableView<ItemInfo> tableView;
    @FXML private TableColumn<ItemInfo, String> colPartNumber;
    @FXML private TableColumn<ItemInfo, String> colDescription;
    @FXML private Button selectButton;
    @FXML private Button cancelButton;

    @FXML
    public void initialize() {
        // Настраиваем колонки
        colPartNumber.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getPartNumber()));
        colDescription.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getDescription()));

        // Загружаем все элементы
        tableView.setItems(FXCollections.observableArrayList(
                itemInfoService.getAll()
        ));

        // Блокируем Select, если ничего не выбрано
        selectButton.disableProperty()
                .bind(tableView.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void onSelect() {
        selectedItem = tableView.getSelectionModel().getSelectedItem();
        closeDialog();
    }

    @FXML
    private void onCancel() {
        selectedItem = null;
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }

    /**
     * Возвращает выбранный ItemInfo или null, если диалог был отменён.
     */
    public ItemInfo getSelectedItem() {
        return selectedItem;
    }
}
