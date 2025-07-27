package com.levare.hultic.ops.joborders.controller;

import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;
import com.levare.hultic.ops.joborders.entity.JobOrderType;
import com.levare.hultic.ops.joborders.service.JobOrderService;
import com.levare.hultic.ops.users.service.UserService;
import com.levare.hultic.ops.workorders.entity.Client;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for creating a new JobOrder from a WorkOrder and Item.
 */
public class NewJobOrderController {

    private final JobOrderService jobOrderService;
    private final ItemService itemService;
    private final UserService userService;

    private Long workOrderId;
    private Long itemId;

    @FXML
    private TextField partNumberField;
    @FXML
    private TextField serialField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<ItemCondition> conditionCombo;
    @FXML
    private ComboBox<Client> ownerCombo;
    @FXML
    private ComboBox<JobOrderType> jobTypeCombo;
    @FXML
    private DatePicker plannedDatePicker;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextArea commentsArea;

    public NewJobOrderController(
            JobOrderService jobOrderService,
            ItemService itemService,
            UserService userService
    ) {
        this.jobOrderService = jobOrderService;
        this.itemService = itemService;
        this.userService = userService;
    }

    /**
     * Инициализация формы данными выбранного item.
     */


    public void initializeRealEquipment(Long workOrderId, Long itemId) {
        this.workOrderId = workOrderId;
        this.itemId = itemId;

        var item = itemService.getById(itemId);
        partNumberField.setText(item.getItemInfo().getPartNumber());
        partNumberField.setDisable(true);
        serialField.setText(item.getSerialNumber());
        serialField.setDisable(true);
        descriptionArea.setText(item.getItemInfo().getDescription());
        descriptionArea.setDisable(true);

        conditionCombo.setItems(FXCollections.observableArrayList(ItemCondition.values()));
        conditionCombo.setValue(item.getItemCondition());
        conditionCombo.setDisable(true);

        ownerCombo.setItems(FXCollections.observableArrayList(Client.values()));
        ownerCombo.setValue(item.getOwnership());
        ownerCombo.setDisable(true);


        // Ограничиваем список JobOrderType в зависимости от типа оборудования
        switch (item.getItemStatus()) {
            case REPAIR_BOOKED, RNE_BOOKED -> {
                if (item.getItemCondition() == ItemCondition.USED) {
                    switch (item.getItemInfo().getItemType()) {
                        case CABLE -> jobTypeCombo.setItems(FXCollections
                                .observableArrayList(JobOrderType.CABLE_REPAIR));
                        case SENSOR ->
                                jobTypeCombo.setItems(FXCollections.observableArrayList(JobOrderType.SENSOR_TEST));
                        default -> jobTypeCombo.setItems(FXCollections.observableArrayList(JobOrderType.DISMANTLE));
                    }
                } else if (item.getItemCondition() == ItemCondition.DISMANTLED) {
                    jobTypeCombo.setItems(FXCollections
                            .observableArrayList(JobOrderType.ASSEMBLY));
                }
            }
            case DISMANTLE_BOOKED -> jobTypeCombo.setItems(FXCollections
                    .observableArrayList(JobOrderType.DISMANTLE));
            case INSPECTION_BOOKED -> {
                switch (item.getItemInfo().getItemType()) {
                    case SENSOR ->
                            jobTypeCombo.setItems(FXCollections.observableArrayList(JobOrderType.SENSOR_TEST, JobOrderType.SENSOR_CONNECTION));
                    default -> jobTypeCombo.setItems(FXCollections.observableArrayList(JobOrderType.INSPECTION));
                }
            }

            default -> {
                jobTypeCombo.setItems(FXCollections
                        .observableArrayList(JobOrderType.values()));
            }
        }

        jobTypeCombo.getSelectionModel().selectFirst();
        commentsArea.clear();
    }

    public void initializeFromJobOrderNew(Long workOrderId, Long itemId) {
        this.workOrderId = workOrderId;
        this.itemId = itemId;

        var item = itemService.getById(itemId);
        partNumberField.setText(item.getItemInfo().getPartNumber());
        serialField.setText(item.getSerialNumber());
        descriptionArea.setText(item.getItemInfo().getDescription());

        conditionCombo.setItems(FXCollections.observableArrayList(ItemCondition.values()));
        conditionCombo.setValue(item.getItemCondition());

        ownerCombo.setItems(FXCollections.observableArrayList(Client.values()));
        ownerCombo.setValue(item.getOwnership());

        // Ограничиваем список JobOrderType в зависимости от типа оборудования
        switch (item.getItemInfo().getItemType()) {

            default:
                // если не подходит ни один кейс, показываем все типы
                jobTypeCombo.setItems(FXCollections.observableArrayList(JobOrderType.values()));
                break;
        }

        jobTypeCombo.getSelectionModel().selectFirst();
        commentsArea.clear();
    }


    @FXML
    private void initialize() {
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());
    }

    @FXML
    private void handleSave() {

        var jo = new JobOrder();
        jo.setWorkOrderId(workOrderId);
        jo.setItemId(itemId);
        jo.setStatus(JobOrderStatus.CREATED);
        jo.setJobOrderType(jobTypeCombo.getValue());
        jo.setComments(commentsArea.getText());
        System.out.println(plannedDatePicker.getValue().toString());
        jo.setPlannedDate(plannedDatePicker.getValue());

        Long jobOrderId = jobOrderService.create(jo).getId();
        itemService.updateWithJobOrder(itemId, jobOrderId);
        close();
    }

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
