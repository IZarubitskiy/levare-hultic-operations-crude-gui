package com.levare.hultic.ops.joborders.controller;

import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;
import com.levare.hultic.ops.joborders.entity.JobOrderType;
import com.levare.hultic.ops.joborders.service.JobOrderService;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.workorders.entity.Client;
import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.users.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for creating a new JobOrder from a WorkOrder and Item.
 */
public class NewJobOrderController {

    private final JobOrderService jobOrderService;
    private final ItemService     itemService;
    private final UserService     userService;

    private Long workOrderId;
    private Long itemId;

    @FXML private TextField partNumberField;
    @FXML private TextField serialField;
    @FXML private TextArea  descriptionArea;
    @FXML private ComboBox<ItemCondition> conditionCombo;
    @FXML private ComboBox<Client>        ownerCombo;
    @FXML private ComboBox<JobOrderType>  jobTypeCombo;
    @FXML private ComboBox<User>          responsibleCombo;
    @FXML private Button availableStockButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private TextArea commentsArea;

    public NewJobOrderController(
            JobOrderService jobOrderService,
            ItemService     itemService,
            UserService     userService
    ) {
        this.jobOrderService = jobOrderService;
        this.itemService     = itemService;
        this.userService     = userService;
    }

    /**
     * Инициализация формы данными выбранного item.
     */
    public void initForWorkAndItem(Long workOrderId, Long itemId) {
        this.workOrderId = workOrderId;
        this.itemId      = itemId;

        var item = itemService.getById(itemId);
        partNumberField.setText(item.getItemInfo().getPartNumber());
        serialField.setText(item.getSerialNumber());
        descriptionArea.setText(item.getItemInfo().getDescription());

        conditionCombo.setItems(FXCollections.observableArrayList(ItemCondition.values()));
        conditionCombo.setValue(item.getItemCondition());

        ownerCombo.setItems(FXCollections.observableArrayList(Client.values()));
        ownerCombo.setValue(item.getOwnership());

        jobTypeCombo.setItems(FXCollections.observableArrayList(JobOrderType.values()));
        jobTypeCombo.getSelectionModel().selectFirst();

        responsibleCombo.setItems(FXCollections.observableArrayList(userService.getAll()));
        commentsArea.clear();

        availableStockButton.setOnAction(e -> {
            // TODO: открыть диалог доступного склада
        });
    }

    /**
     * Настройка графики и кнопок.
     */
    @FXML
    private void initialize() {
        // Настройка отображения списка пользователей
        responsibleCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getName());
            }
        });
        responsibleCombo.setButtonCell(responsibleCombo.getCellFactory().call(null));

        // Привязка действий к кнопкам
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());
    }

    /**
     * Сохранение новой JobOrder.
     */
    @FXML
    private void handleSave() {
        User resp = responsibleCombo.getValue();
        if (resp == null) {
            showAlert("Validation Error", "Select a responsible user.");
            return;
        }
        var jo = new JobOrder();
        jo.setWorkOrderId(workOrderId);
        jo.setItemId(itemId);
        jo.setStatus(JobOrderStatus.CREATED);
        jo.setResponsibleUser(resp);
        jo.setJobOrderType(jobTypeCombo.getValue());
        jo.setComments(commentsArea.getText());

        jobOrderService.create(jo);
        close();
    }

    /**
     * Отмена и закрытие формы.
     */
    @FXML
    private void handleCancel() {
        close();
    }

    /**
     * Закрытие окна.
     */
    private void close() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String header, String message) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
