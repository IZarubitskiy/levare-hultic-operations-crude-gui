package com.levare.hultic.ops;

import com.levare.hultic.ops.login.LoginController;
import com.levare.hultic.ops.users.dao.UserDao;
import com.levare.hultic.ops.users.service.UserServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Entry point for Levare Hultic Operations JavaFX GUI application with login.
 */
public class LevareHulticOperationsApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // DB setup
            Connection connection = DriverManager.getConnection("jdbc:sqlite:data.db");
            UserDao userDao = new UserDao(connection);
            UserServiceImpl userService = new UserServiceImpl(userDao);

            // Load login window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene loginScene = new Scene(loader.load());

            // Inject dependencies into login controller
            LoginController loginController = loader.getController();
            loginController.setStage(primaryStage);

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
