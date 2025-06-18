package com.levare.hultic.ops.iteminfos.controller;

import com.levare.hultic.ops.common.ConnectionFactory;
import com.levare.hultic.ops.iteminfos.dao.ItemInfoDao;
import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import com.levare.hultic.ops.iteminfos.service.ItemInfoServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ItemInfoSelectionController {

    @FXML private TableView<ItemInfo> tableView;
    @FXML private TableColumn<ItemInfo, String> colPartNumber;
    @FXML private TableColumn<ItemInfo, String> colDescription;
    @FXML private Button selectButton;
    @FXML private Button cancelButton;

    private final ItemInfoService itemInfoService;
    private ItemInfo selectedItem;

    public ItemInfoSelectionController() {
        try {
            // Инициализируем DAO и сервис через ConnectionFactory
            this.itemInfoService = new ItemInfoServiceImpl(
                    new ItemInfoDao(ConnectionFactory.getConnection())
            );
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open database connection for ItemInfoService", e);
        }
    }

    @FXML
    public void initialize() {
        // Настраиваем колонки таблицы
        colPartNumber.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getPartNumber())
        );
        colDescription.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getDescription())
        );

        // Загружаем все записи из сервиса
        tableView.setItems(FXCollections.observableArrayList(itemInfoService.getAll()));

        // Блокируем кнопку Select, если ничего не выбрано
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
