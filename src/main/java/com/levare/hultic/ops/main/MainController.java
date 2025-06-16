package com.levare.hultic.ops.main;

import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.workorders.controller.WorkOrderController;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class MainController {

    @FXML private Label userLabel;
    @FXML private TabPane tabPane;
    @FXML private AnchorPane contentArea;

    private Node workOrdersContent;
    private User currentUser;
    private WorkOrderService workOrderService; // передаётся из App

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (userLabel != null) {
            userLabel.setText("Logged in as: " + user.getName() + " (" + user.getPosition() + ")");
        }
    }

    public void setWorkOrderService(WorkOrderService service) {
        this.workOrderService = service;
    }

    @FXML
    public void initialize() {
        if (currentUser != null) {
            userLabel.setText("Logged in as: " + currentUser.getName() + " (" + currentUser.getPosition() + ")");
        }

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                String tabId = newTab.getId();
                if ("workOrdersTab".equals(tabId)) {
                    loadWorkOrdersView();
                }
                // можешь сюда добавить и другие вкладки
            }
        });
    }

    private void loadWorkOrdersView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/workorders.fxml"));
            Node node = loader.load();

            WorkOrderController controller = loader.getController();
            controller.setWorkOrderService(workOrderService);

            // Вставляем в интерфейс
            contentArea.getChildren().setAll(node);
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

            // ⏳ Отложенный вызов — после GUI загрузки
            javafx.application.Platform.runLater(controller::refreshTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
