package com.levare.hultic.ops.iteminfos.controller;

import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ItemInfoSelectionController {

    @FXML
    private TextField filterPartField;
    @FXML
    private TextField filterDescField;

    @FXML
    private TableView<ItemInfo> tableView;
    @FXML
    private TableColumn<ItemInfo, String> colPartNumber;
    @FXML
    private TableColumn<ItemInfo, String> colDescription;
    @FXML
    private TableColumn<ItemInfo, String> colItemType;
    @FXML
    private TableColumn<ItemInfo, String> colComments;

    @FXML
    private TextField partNumberField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField itemTypeField;
    @FXML
    private TextField commentsField;

    @FXML
    private Button selectButton;
    @FXML
    private Button cancelButton;

    private final ItemInfoService itemInfoService;
    private FilteredList<ItemInfo> filteredData;
    private ItemInfo selectedItem;

    public ItemInfoSelectionController(ItemInfoService service) {
        this.itemInfoService = service;
    }

    @FXML
    public void initialize() {
        // Настройка колонок
        colPartNumber.setCellValueFactory(c ->
                Bindings.createStringBinding(() -> c.getValue().getPartNumber()));
        colDescription.setCellValueFactory(c ->
                Bindings.createStringBinding(() -> c.getValue().getDescription()));
        colItemType.setCellValueFactory(c ->
                Bindings.createStringBinding(() -> c.getValue().getItemType().name()));
        colComments.setCellValueFactory(c ->
                Bindings.createStringBinding(() -> c.getValue().getComments()));

        // Загружаем и фильтруем
        filteredData = new FilteredList<>(
                FXCollections.observableArrayList(itemInfoService.getAll()),
                p -> true
        );
        tableView.setItems(filteredData);

        // Кнопка Select активна только при выделении
        selectButton.disableProperty()
                .bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        // Слушатель фильтров
        filterPartField.textProperty().addListener((o, old, nw) -> applyFilter());
        filterDescField.textProperty().addListener((o, old, nw) -> applyFilter());

        // **Новый** слушатель выбора строки:
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                partNumberField.setText(newItem.getPartNumber());
                descriptionField.setText(newItem.getDescription());
                itemTypeField.setText(newItem.getItemType().name());
                commentsField.setText(newItem.getComments());
            } else {
                partNumberField.clear();
                descriptionField.clear();
                itemTypeField.clear();
                commentsField.clear();
            }
        });
    }

    private void applyFilter() {
        String rawPart = filterPartField.getText().trim();
        String rawDesc = filterDescField.getText().trim();
        String regexPart = wildcardToRegex(rawPart);
        String regexDesc = wildcardToRegex(rawDesc);

        Predicate<ItemInfo> pred = item -> {
            boolean m1 = regexPart.isEmpty() ||
                    Pattern.compile(regexPart, Pattern.CASE_INSENSITIVE)
                            .matcher(item.getPartNumber()).matches();
            boolean m2 = regexDesc.isEmpty() ||
                    Pattern.compile(regexDesc, Pattern.CASE_INSENSITIVE)
                            .matcher(item.getDescription()).matches();
            return m1 && m2;
        };

        filteredData.setPredicate(pred);
    }

    private String wildcardToRegex(String w) {
        if (w.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("^");
        for (char c : w.toCharArray()) {
            if (c == '*') sb.append(".*");
            else {
                if ("\\.^$+?{}[]|()".indexOf(c) >= 0) sb.append("\\");
                sb.append(c);
            }
        }
        return sb.append("$").toString();
    }

    @FXML
    private void onSelect() {
        selectedItem = tableView.getSelectionModel().getSelectedItem();
        ((Stage) tableView.getScene().getWindow()).close();
    }

    @FXML
    private void onCancel() {
        selectedItem = null;
        ((Stage) tableView.getScene().getWindow()).close();
    }

    public ItemInfo getSelectedItem() {
        return selectedItem;
    }
}
