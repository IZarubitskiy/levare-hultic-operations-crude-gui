package com.levare.hultic.ops.joborders.controller;

import com.levare.hultic.ops.common.ExcelTemplateService;
import com.levare.hultic.ops.items.controller.FilteredItemSelectionController;
import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;
import com.levare.hultic.ops.joborders.entity.JobOrderType;
import com.levare.hultic.ops.joborders.service.JobOrderService;
import com.levare.hultic.ops.users.service.UserService;
import com.levare.hultic.ops.workorders.entity.Client;
import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JobOrderController {

    private final JobOrderService jobOrderService;
    private final UserService userService;
    private final ItemService itemService;
    private final WorkOrderService workOrderService;
    private final ExcelTemplateService excelService;
    private final Callback<Class<?>, Object> controllerFactory;
     // теперь через конструктор

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML private TableView<JobOrder> jobOrderTable;
    @FXML private TableColumn<JobOrder, Long> idColumn;
    @FXML private TableColumn<JobOrder, String> partNumberColumn;
    @FXML private TableColumn<JobOrder, String> serialColumn;
    @FXML private TableColumn<JobOrder, String> descriptionColumn;
    @FXML private TableColumn<JobOrder, String> clientColumn;
    @FXML private TableColumn<JobOrder, JobOrderType> jobTypeColumn;
    @FXML private TableColumn<JobOrder, JobOrderStatus> statusColumn;
    @FXML private TableColumn<JobOrder, String> wellColumn;
    @FXML private TableColumn<JobOrder, String> plannedDateColumn;
    @FXML private TableColumn<JobOrder, LocalDate> deliveryColumn;
    @FXML private TableColumn<JobOrder, String> commentsColumn;

    @FXML private ComboBox<JobOrderStatus> statusFilterCombo;
    @FXML private Button refreshButton;
    @FXML private Button deleteButton;
    @FXML private Button createButton;
    @FXML private Button updateButton;
    @FXML private Button finishButton;
    @FXML private Button cancelButton;
    @FXML private Button printButton;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(c ->
                new SimpleLongProperty(c.getValue().getId()).asObject()
        );

        partNumberColumn.setCellValueFactory(c -> {
            Item it = itemService.getById(c.getValue().getItemId());
            return new SimpleStringProperty(it.getItemInfo().getPartNumber());
        });
        serialColumn.setCellValueFactory(c -> {
            Item it = itemService.getById(c.getValue().getItemId());
            return new SimpleStringProperty(it.getSerialNumber());
        });
        descriptionColumn.setCellValueFactory(c -> {
            Item it = itemService.getById(c.getValue().getItemId());
            return new SimpleStringProperty(it.getItemInfo().getDescription());
        });

        clientColumn.setCellValueFactory(c -> {
            Item it = itemService.getById(c.getValue().getItemId());
            return new SimpleStringProperty(it.getOwnership().name());
        });

        deliveryColumn.setCellValueFactory(c -> {
            Long woId = c.getValue().getWorkOrderId();
            if (woId == null || woId == 0) {
                return new SimpleObjectProperty<>(null);
            }
            WorkOrder wo = workOrderService.getById(woId);
            return new SimpleObjectProperty<>(wo.getDeliveryDate());
        });
        jobTypeColumn.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getJobOrderType())
        );
        statusColumn.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getStatus())
        );

        wellColumn.setCellValueFactory(c -> {
            Long woId = c.getValue().getWorkOrderId();
            if (woId == null || woId == 0) {
                return new SimpleStringProperty("");
            }
            WorkOrder wo = workOrderService.getById(woId);
            return new SimpleStringProperty(wo.getWell());
        });
        plannedDateColumn.setCellValueFactory(c ->
        {
            if (c.getValue().getPlannedDate().isAfter(c.getValue().getPlannedDateUpdated())) {
                return new SimpleObjectProperty<> (c.getValue().getPlannedDate().toString());
            } else {
                return new SimpleObjectProperty<> (c.getValue().getPlannedDateUpdated().toString());
            }
        }
        );

        commentsColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getComments())
        );

        statusFilterCombo.setItems(FXCollections.observableArrayList(JobOrderStatus.values()));
        statusFilterCombo.getItems().add(0, null);

        refreshButton.setOnAction(e -> refreshTable());
        createButton.setOnAction(e -> createNew());
        updateButton.setOnAction(e -> updateSelected());
        deleteButton.setOnAction(e -> deleteSelected());
        printButton.setOnAction(e -> handlePrint());

        refreshTable();
    }

    public void refreshTable() {
        List<JobOrder> list = statusFilterCombo.getValue() != null
                ? jobOrderService.getByStatus(statusFilterCombo.getValue())
                : jobOrderService.getAll();
        jobOrderTable.setItems(FXCollections.observableArrayList(list));
    }
    @FXML
    private void createNew() {
        try {
            FXMLLoader pickLoader = new FXMLLoader(
                    getClass().getResource("/fxml/filtered_item_selection.fxml")
            );
            pickLoader.setControllerFactory(cls -> {
                if (cls == FilteredItemSelectionController.class) {
                    return new FilteredItemSelectionController(
                            itemService,
                            List.of(Client.RNE, Client.CORPORATE),
                            List.of(),
                            List.of(ItemStatus.ON_STOCK)
                    );
                }
                return controllerFactory.call(cls);
            });
            Parent pickRoot = pickLoader.load();
            Stage pickStage = new Stage();
            pickStage.initModality(Modality.APPLICATION_MODAL);
            pickStage.setTitle("Select Equipment for Job Order");
            pickStage.setScene(new Scene(pickRoot));
            pickStage.showAndWait();

            Item chosen = pickLoader.<FilteredItemSelectionController>getController().getSelectedItem();
            if (chosen == null) return;

            FXMLLoader joLoader = new FXMLLoader(
                    getClass().getResource("/fxml/new_job_order.fxml")
            );
            joLoader.setControllerFactory(controllerFactory);
            Parent joRoot = joLoader.load();

            var joCtrl = joLoader.<NewJobOrderController>getController();
            joCtrl.initForWorkAndItem(null, chosen.getId());

            Stage joStage = new Stage();
            joStage.initModality(Modality.APPLICATION_MODAL);
            joStage.setTitle("New Job Order");
            joStage.setScene(new Scene(joRoot));
            joStage.showAndWait();

            refreshTable();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Cannot create JobOrder:\n" + ex.getMessage());
        }
    }

    private void handlePrint() {
        JobOrder sel = jobOrderTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("No selection", "Please select a job order to print.");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        StringBuilder joNumber = new StringBuilder(11);

        joNumber.append("JO");
        switch (String.valueOf(sel.getId()).length()) {
            case 1 -> joNumber.append("0000");
            case 2 -> joNumber.append("000");
            case 3 -> joNumber.append("00");
            case 4 -> joNumber.append("0");
            case 5 -> joNumber.append("");
            default -> throw new IllegalStateException(
                    "Unexpected JobOrder number : " + sel.getId());
        }
        joNumber.append(sel.getId());
        data.put("orderId",joNumber);

        Item it = itemService.getById(sel.getItemId());
        data.put("partNumber", it.getItemInfo().getPartNumber());
        data.put("serialNumber", it.getSerialNumber());
        data.put("description", it.getItemInfo().getDescription());
        data.put("clientName", it.getOwnership().name());
        data.put("date", LocalDate.now());
        String wellNumber = sel.getWorkOrderId() == 0
                ? ""
                : workOrderService.getById(sel.getWorkOrderId()).getWorkOrderNumber();
        data.put("wellNumber", wellNumber);

        data.put("comments", sel.getComments());

        String templateName = "";
        switch (it.getItemInfo().getItemType()) {
            case PUMP, VAPRO -> {
                    switch (sel.getJobOrderType()){
                        case DISMANTLE -> templateName = "jo_pump_dismantle_template.xlsx";
                        case ASSEMBLY -> templateName = "jo_pump_assembly_template.xlsx";
                        case INSPECTION -> templateName = "jo_pump_inspection_template.xlsx";
                        default -> throw new IllegalStateException(
                                "Unexpected JobOrder type : " + sel.getJobOrderType());
                    }
            }
            case BOI, GAS_SEPARATOR -> {
                switch (sel.getJobOrderType()){
                case DISMANTLE -> templateName = "jo_intake_device_dismantle_template.xlsx";
                case ASSEMBLY -> templateName = "jo_intake_device_assembly_template.xlsx";
                case INSPECTION -> templateName = "jo_intake_device_inspection_template.xlsx";
                default -> throw new IllegalStateException(
                        "Unexpected JobOrder type : " + sel.getJobOrderType());
                }
            }
            case SEAL -> {
                switch (sel.getJobOrderType()){
                    case DISMANTLE -> templateName = "jo_seal_dismantle_template.xlsx";
                    case ASSEMBLY -> templateName = "jo_seal_assembly_template.xlsx";
                    case INSPECTION -> templateName = "jo_seal_inspection_template.xlsx";
                    case TANDEM -> templateName = "jo_seal_tandem_template.xlsx";
                    default -> throw new IllegalStateException(
                            "Unexpected JobOrder type : " + sel.getJobOrderType());
                }
            }
            case MOTOR -> {
                switch (sel.getJobOrderType()){
                    case DISMANTLE -> templateName = "jo_motor_dismantle_template.xlsx";
                    case ASSEMBLY -> templateName = "jo_motor_assembly_template.xlsx";
                    case INSPECTION -> templateName = "jo_motor_inspection_template.xlsx";
                    default -> throw new IllegalStateException(
                            "Unexpected JobOrder type : " + sel.getJobOrderType());
                }
            }

            case SENSOR -> {
                switch (sel.getJobOrderType()){
                    case INSPECTION -> templateName = "jo_sensor_inspection_template.xlsx";
                    case SENSOR_CONNECTION -> templateName = "jo_sensor_connection_template.xlsx";
                    default -> throw new IllegalStateException(
                            "Unexpected JobOrder type : " + sel.getJobOrderType());
                }
            }
            case MLE -> {
                switch (sel.getJobOrderType()){
                    case CABLE_CUT -> templateName = "jo_mle_cable_cut_template.xlsx";
                    case ASSEMBLY -> templateName = "jo_mle_assembly_template.xlsx";
                    case INSPECTION -> templateName = "jo_mle_inspection_template.xlsx";
                    default -> throw new IllegalStateException(
                            "Unexpected JobOrder type : " + sel.getJobOrderType());
                }
            }
            case CABLE -> {
                switch (sel.getJobOrderType()){
                    case CABLE_REPAIR -> templateName = "jo_cable_repair_template.xlsx";
                    case INSPECTION -> templateName = "jo_cable_inspection_template.xlsx";
                    default -> throw new IllegalStateException(
                            "Unexpected JobOrder type : " + sel.getJobOrderType());
                }
            }
        }

        Path output = Paths.get(System.getProperty("user.dir"), "out",
                "JobOrder_" + sel.getId() + ".xlsx");

        try {
            Path result = excelService.generate(templateName, data, output);
            Desktop.getDesktop().open(result.toFile());
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "Cannot generate or open file: " + ex.getMessage());
        }

        jobOrderService.changeStatus(sel.getId(), JobOrderStatus.IN_PROGRESS);
    }

    private void updateSelected() {
        JobOrder sel = jobOrderTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("No selection", "Please select a Job Order first.");
            return;
        }

        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle("Change Planned Date");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        DatePicker picker = new DatePicker(sel.getPlannedDate());
        dialog.getDialogPane().setContent(picker);

        dialog.setResultConverter(btn -> btn == ButtonType.OK ? picker.getValue() : null);
        dialog.showAndWait().ifPresent(newDate -> {
            if (newDate != null) {
                if (newDate.isAfter(workOrderService.getById(sel.getWorkOrderId()).getDeliveryDate()))
                {
                    showAlert("Wrong date", "Planned date is later than delivery date.");
                    return;
                    }
                sel.setPlannedDateUpdated(newDate);
                jobOrderService.update(sel.getId(), sel);
                refreshTable();
            }
        });
    }

    @FXML
    private void deleteSelected() {
        JobOrder sel = jobOrderTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("No selection", "Please select a job order to delete.");
            return;
        }
        TextInputDialog reasonDialog = new TextInputDialog();
        reasonDialog.setTitle("Delete Job Order");
        reasonDialog.setHeaderText("Please provide a reason for deletion:");
        reasonDialog.setContentText("Reason:");
        var result = reasonDialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String reason = result.get().trim();
            itemService.updateWithJobOrder(sel.getItemId(), null);
            jobOrderService.delete(sel.getId(), reason);
            refreshTable();
        } else {
            showAlert("Validation", "Deletion reason cannot be empty.");
        }
    }



    private void showAlert(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
