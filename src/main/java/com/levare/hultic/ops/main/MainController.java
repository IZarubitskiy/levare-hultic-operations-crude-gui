package com.levare.hultic.ops.main;

import com.levare.hultic.ops.workorders.controller.WorkOrderController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.levare.hultic.ops.users.entity.User;

public class MainController {

    @FXML private Label userLabel;
    @FXML private TabPane tabPane;
    @FXML private AnchorPane contentArea;

    private final Callback<Class<?>,Object> controllerFactory;
    private User currentUser;
    private Stage stage;  // <-- add this

    public MainController(Callback<Class<?>,Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    /** Called by the factory after login */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (userLabel != null) {
            userLabel.setText("Logged in as: " + user.getName() +
                    " (" + user.getPosition() + ")");
        }
    }

    /** Expose currentUser for callers like LoginController */
    public User getCurrentUser() {
        return currentUser;
    }

    /** Receive the primary Stage so we can use it later if needed */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldTab, newTab) -> {
                    if (newTab != null && "workOrdersTab".equals(newTab.getId())) {
                        loadWorkOrdersView();
                    }
                });

        Platform.runLater(() ->
                tabPane.getSelectionModel().select(tabPane.getTabs().get(0))
        );
    }

    private void loadWorkOrdersView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/workorders.fxml"));
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            WorkOrderController ctrl = loader.getController();

            contentArea.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root,    0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root,   0.0);
            AnchorPane.setRightAnchor(root,  0.0);

            Platform.runLater(ctrl::refreshTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
