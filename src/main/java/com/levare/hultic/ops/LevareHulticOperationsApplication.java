package com.levare.hultic.ops;

import com.levare.hultic.ops.common.ConnectionFactory;
import com.levare.hultic.ops.common.DatabaseInitializer;
import com.levare.hultic.ops.iteminfos.dao.ItemInfoDao;
import com.levare.hultic.ops.iteminfos.service.ItemInfoServiceImpl;
import com.levare.hultic.ops.items.dao.ItemDao;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.items.service.ItemServiceImpl;
import com.levare.hultic.ops.login.LoginController;
import com.levare.hultic.ops.users.dao.UserDao;
import com.levare.hultic.ops.users.service.UserService;
import com.levare.hultic.ops.users.service.UserServiceImpl;
import com.levare.hultic.ops.workorders.dao.WorkOrderDao;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import com.levare.hultic.ops.workorders.service.WorkOrderServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.Connection;

/**
 * Entry point for Levare Hultic Operations JavaFX GUI application with login.
 */
public class LevareHulticOperationsApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Подключение к БД
            Connection connection = ConnectionFactory.getConnection();
            DatabaseInitializer.initialize(connection);

            // DAO
            UserDao userDao = new UserDao(connection);
            ItemDao itemDao = new ItemDao(connection);
            ItemInfoDao itemInfoDao = new ItemInfoDao(connection);
            WorkOrderDao workOrderDao = new WorkOrderDao(connection);

            // Сервисы
            UserServiceImpl userService = new UserServiceImpl(userDao);
            ItemInfoServiceImpl itemInfoService = new ItemInfoServiceImpl(itemInfoDao);
            ItemServiceImpl itemService = new ItemServiceImpl(itemDao, itemInfoService);
            WorkOrderServiceImpl workOrderService = new WorkOrderServiceImpl(workOrderDao, userService, itemService);

            // Загрузка логина
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene loginScene = new Scene(loader.load());

            LoginController loginController = loader.getController();
            loginController.setStage(primaryStage);
            loginController.setWorkOrderService(workOrderService);
            primaryStage.setTitle("Login - Levare Hultic Operations");
            primaryStage.setScene(loginScene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showStartupError(e.getMessage());
        }
    }

    private void showStartupError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Startup Error");
        alert.setHeaderText("Failed to start application");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
