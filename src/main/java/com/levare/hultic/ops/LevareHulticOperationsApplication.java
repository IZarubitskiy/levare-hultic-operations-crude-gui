package com.levare.hultic.ops;

import com.levare.hultic.ops.common.ConnectionFactory;
import com.levare.hultic.ops.common.DatabaseInitializer;
import com.levare.hultic.ops.iteminfos.dao.ItemInfoDao;
import com.levare.hultic.ops.iteminfos.service.ItemInfoServiceImpl;
import com.levare.hultic.ops.items.dao.ItemDao;
import com.levare.hultic.ops.items.service.ItemServiceImpl;
import com.levare.hultic.ops.login.LoginController;
import com.levare.hultic.ops.users.dao.UserDao;
import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.users.service.UserServiceImpl;
import com.levare.hultic.ops.workorders.dao.WorkOrderDao;
import com.levare.hultic.ops.workorders.entity.Client;
import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;
import com.levare.hultic.ops.workorders.service.WorkOrderServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;

/**
 * Entry point for Levare Hultic Operations JavaFX GUI application with login.
 */
public class LevareHulticOperationsApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Database connection and init
            Connection connection = ConnectionFactory.getConnection();
            DatabaseInitializer.initialize(connection);

            // DAOs
            UserDao userDao = new UserDao(connection);
            ItemDao itemDao = new ItemDao(connection);
            ItemInfoDao itemInfoDao = new ItemInfoDao(connection);
            WorkOrderDao workOrderDao = new WorkOrderDao(connection);

            // Services
            UserServiceImpl userService = new UserServiceImpl(userDao);
            ItemInfoServiceImpl itemInfoService = new ItemInfoServiceImpl(itemInfoDao);
            ItemServiceImpl itemService = new ItemServiceImpl(itemDao, itemInfoService);
            WorkOrderServiceImpl workOrderService = new WorkOrderServiceImpl(workOrderDao, userService, itemService);

            if (userService.getAll().isEmpty()) {
                User demoUser = new User(null, "Demo User", "WSManager");
                userService.create(demoUser);
                System.out.println("👤 Demo user created.");
            }

            // 👇 Добавим демо-запись, если таблица пустая
            if (workOrderService.getAll().isEmpty()) {
                WorkOrder demo = new WorkOrder();
                demo.setWorkOrderNumber("WO-0001");
                demo.setClient(Client.METCO); // замените на свой enum Client
                demo.setWell("Well-Alpha");
                demo.setRequestDate(LocalDate.now());
                demo.setDeliveryDate(LocalDate.now().plusDays(5));
                demo.setStatus(WorkOrderStatus.CREATED);
                demo.setComments("Created automatically at startup");

                // Добавим фиктивного пользователя как requestor, если есть
                User requestor = userService.getAll().stream().findFirst().orElse(null);
                demo.setRequestor(requestor);

                workOrderService.create(demo);
                System.out.println("📥 Demo WorkOrder created.");
            }

            // GUI login
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