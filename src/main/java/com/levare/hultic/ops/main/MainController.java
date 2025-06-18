package com.levare.hultic.ops.main;

import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.workorders.controller.WorkOrderController;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
            Parent root = loader.load(); // ✅ Тип Parent гарантирует наличие layout-методов

            WorkOrderController controller = loader.getController();
            controller.setWorkOrderService(workOrderService);

            contentArea.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

            // ⏳ Отложенный рендеринг, который точно сработает
            javafx.application.Platform.runLater(() -> {
                root.requestLayout();             // ✅ гарантированно есть
                root.applyCss();                 // ⬅️ дополняем
                root.layout();                   // ⬅️ принудительный layout pass
                controller.refreshTable();       // ✅ если нужно
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
