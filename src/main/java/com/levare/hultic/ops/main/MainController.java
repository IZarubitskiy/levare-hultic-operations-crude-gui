package com.levare.hultic.ops.main;

import com.levare.hultic.ops.joborders.controller.JobOrderController;
import com.levare.hultic.ops.workorders.controller.WorkOrderController;
import com.levare.hultic.ops.tracking.controller.TrackingRecordController;
import com.levare.hultic.ops.users.entity.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainController {

    @FXML private Label userLabel;
    @FXML private TabPane tabPane;
    @FXML private AnchorPane contentArea;

    private final Callback<Class<?>, Object> controllerFactory;
    private User currentUser;
    private Stage stage;

    public MainController(Callback<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (userLabel != null) {
            userLabel.setText("Logged in as: " + user.getName() + " (" + user.getPosition() + ")");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldTab, newTab) -> {
                    if (newTab != null) {
                        String id = newTab.getId();
                        if ("workOrdersTab".equals(id)) {
                            loadWorkOrdersView();
                        } else if ("jobOrdersTab".equals(id)) {
                            loadJobOrdersView();
                        } else if ("trackingTab".equals(id)) {
                            loadTrackingRecordsView();
                        }
                    }
                });
        // Select first tab on startup
        Platform.runLater(() -> tabPane.getSelectionModel().select(0));
    }

    private void loadWorkOrdersView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/workorders.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();
            WorkOrderController ctrl = loader.getController();

            contentArea.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

            Platform.runLater(ctrl::refreshTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadJobOrdersView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/joborders.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();
            JobOrderController ctrl = loader.getController();

            contentArea.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

            Platform.runLater(ctrl::refreshTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTrackingRecordsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tracking_records.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();
            TrackingRecordController ctrl = loader.getController();

            contentArea.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

            Platform.runLater(ctrl::refreshTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
