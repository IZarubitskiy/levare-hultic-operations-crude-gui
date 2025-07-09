package com.levare.hultic.ops.joborders.controller;

import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.entity.JobOrderStatus;
import com.levare.hultic.ops.joborders.service.JobOrderService;
import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.users.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Stub controller for creating a new JobOrder from a WorkOrder and Item.
 */
public class NewJobOrderController {

    private final JobOrderService jobOrderService;
    private final UserService     userService;

    private Long workOrderId;
    private Long itemId;

    @FXML private ComboBox<User> responsibleCombo;
    @FXML private TextArea commentsArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    public NewJobOrderController(JobOrderService jobOrderService, UserService userService) {
        this.jobOrderService = jobOrderService;
        this.userService     = userService;
    }

    /**
     * Initialize controller for given WorkOrder and Item IDs.
     */
    public void initForWorkAndItem(Long workOrderId, Long itemId) {
        this.workOrderId = workOrderId;
        this.itemId      = itemId;
        // Populate responsible users
        responsibleCombo.setItems(FXCollections.observableArrayList(userService.getAll()));
    }

    @FXML
    private void initialize() {
        // Setup ComboBox display
        responsibleCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getName());
            }
        });
        responsibleCombo.setButtonCell(responsibleCombo.getCellFactory().call(null));
    }

    /**
     * Handle Save action: create JobOrder and close dialog.
     */
    @FXML
    private void handleSave() {
        User resp = responsibleCombo.getValue();
        if (resp == null) {
            showAlert("Validation Error", "Select a responsible user.");
            return;
        }
        JobOrder jo = new JobOrder();
        jo.setWorkOrderId(workOrderId);
        jo.setItemId(itemId);
        jo.setStatus(JobOrderStatus.CREATED);
        jo.setResponsibleUser(resp);
        jo.setComments(commentsArea.getText());
        jobOrderService.create(jo);
        close();
    }

    /**
     * Cancel and close the dialog.
     */
    @FXML
    private void handleCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}