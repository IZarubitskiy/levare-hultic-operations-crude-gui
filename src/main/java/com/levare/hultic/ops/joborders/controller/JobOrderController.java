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
import com.levare.hultic.ops.users.entity.User;
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
    @FXML private TableColumn<JobOrder, String> responsibleColumn;
    @FXML private TableColumn<JobOrder, LocalDate> deliveryColumn;
    @FXML private TableColumn<JobOrder, String> commentsColumn;

    @FXML private ComboBox<JobOrderStatus> statusFilterCombo;
    @FXML private Button refreshButton;
    @FXML private Button deleteButton;
    @FXML private Button createButton;
    @FXML private Button updateButton;
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
        responsibleColumn.setCellValueFactory(c -> {
            User u = c.getValue().getResponsibleUser();
            return new SimpleStringProperty(u != null ? u.getName() : "");
        });
        commentsColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getComments())
        );

        statusFilterCombo.setItems(FXCollections.observableArrayList(JobOrderStatus.values()));
        statusFilterCombo.getItems().add(0, null);

        refreshButton.setOnAction(e -> refreshTable());
        deleteButton.setOnAction(e -> deleteSelected());
        createButton.setOnAction(e -> createNew());
        updateButton.setOnAction(e -> updateSelected());
        printButton.setOnAction(e -> handlePrint());

        refreshTable();
    }

    public void refreshTable() {
        List<JobOrder> list = statusFilterCombo.getValue() != null
                ? jobOrderService.getByStatus(statusFilterCombo.getValue())
                : jobOrderService.getAll();
        jobOrderTable.setItems(FXCollections.observableArrayList(list));
    }

    private void handlePrint() {
        JobOrder sel = jobOrderTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("No selection", "Please select a job order to print.");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", sel.getId());
        Item it = itemService.getById(sel.getItemId());
        data.put("partNumber", it.getItemInfo().getPartNumber());
        data.put("serialNumber", it.getSerialNumber());
        data.put("description", sel.getComments());
        data.put("clientName", it.getOwnership().name());
        data.put("status", sel.getStatus().toString());
        data.put("responsible", sel.getResponsibleUser() != null
                ? sel.getResponsibleUser().getName() : "");
        data.put("deliveryDate", workOrderService.getById(sel.getWorkOrderId()).getDeliveryDate());
        data.put("comments", sel.getComments());

        String templateName = "JobOrderTemplate.xlsx";
        Path output = Paths.get(System.getProperty("user.dir"), "out",
                "JobOrder_" + sel.getId() + ".xlsx");

        try {
            Path result = excelService.generate(templateName, data, output);
            Desktop.getDesktop().open(result.toFile());
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "Cannot generate or open file: " + ex.getMessage());
        }
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

    private void updateSelected() {
        JobOrder sel = jobOrderTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            sel.setComments(sel.getComments() + " (updated)");
            jobOrderService.update(sel.getId(), sel);
            refreshTable();
        } else {
            showAlert("No selection", "Please select a job order to update.");
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
