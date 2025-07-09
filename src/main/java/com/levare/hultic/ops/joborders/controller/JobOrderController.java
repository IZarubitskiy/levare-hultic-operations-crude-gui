package com.levare.hultic.ops.joborders.controller;

import com.levare.hultic.ops.items.controller.FilteredItemSelectionController;
import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.joborders.controller.NewJobOrderController;
import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;
import com.levare.hultic.ops.joborders.service.JobOrderService;
import com.levare.hultic.ops.users.service.UserService;
import com.levare.hultic.ops.workorders.entity.Client;
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

import java.util.List;

@RequiredArgsConstructor
public class JobOrderController {

    private final JobOrderService    jobOrderService;
    private final UserService        userService;
    private final ItemService        itemService;
    private final Callback<Class<?>, Object> controllerFactory;

    @FXML private TableView<JobOrder> jobOrderTable;
    @FXML private TableColumn<JobOrder, Long>        idColumn;
    @FXML private TableColumn<JobOrder, JobOrderStatus> statusColumn;
    @FXML private TableColumn<JobOrder, String>      commentsColumn;

    @FXML private ComboBox<JobOrderStatus> statusFilterCombo;
    @FXML private Button refreshButton, deleteButton, createButton, updateButton;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleLongProperty(c.getValue().getId()).asObject());
        statusColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getStatus()));
        commentsColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getComments()));

        statusFilterCombo.setItems(FXCollections.observableArrayList(JobOrderStatus.values()));
        refreshButton.setOnAction(e -> refreshTable());
        deleteButton .setOnAction(e -> deleteSelected());
        createButton .setOnAction(e -> createNew());
        updateButton .setOnAction(e -> updateSelected());

        refreshTable();
    }

    public void refreshTable() {
        List<JobOrder> list = statusFilterCombo.getValue() != null
                ? jobOrderService.getByStatus(statusFilterCombo.getValue())
                : jobOrderService.getAll();
        jobOrderTable.setItems(FXCollections.observableArrayList(list));
    }

    private void deleteSelected() {
        JobOrder sel = jobOrderTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            jobOrderService.delete(sel.getId());
            refreshTable();
        } else {
            showAlert("No selection", "Please select a job order to delete.");
        }
    }

    /**
     * Открывает сначала выбор Item-а, затем форму создания JobOrder для него.
     */
    @FXML
    private void createNew() {
        try {
            // 1) Диалог выбора оборудования (весь склад: client=null)
            FXMLLoader pickLoader = new FXMLLoader(
                    getClass().getResource("/fxml/filtered_item_selection.fxml")
            );
            pickLoader.setControllerFactory(cls -> {
                if (cls == FilteredItemSelectionController.class) {
                    return new FilteredItemSelectionController(
                            itemService,
                            FilteredItemSelectionController.Mode.STOCK,
                            Client.METCO    // можно заменить на конкретного клиента
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
            if (chosen == null) return;  // отмена

            // 2) Диалог создания JobOrder для выбранного Item
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
