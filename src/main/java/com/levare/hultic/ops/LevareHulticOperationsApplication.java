package com.levare.hultic.ops;

import com.levare.hultic.ops.common.AppControllerFactory;
import com.levare.hultic.ops.common.ServiceRegistry;
import com.levare.hultic.ops.login.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class LevareHulticOperationsApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Инициализация всех DAO и сервисов происходит в ServiceRegistry
            // (подключение к БД, DatabaseInitializer, демо-данные)
            Class<?> ignore = ServiceRegistry.class;

            // Фабрика контроллеров, она инжектит во все контроллеры нужные сервисы
            AppControllerFactory controllerFactory = new AppControllerFactory();

            // Загрузка экрана логина через FXMLLoader с нашей фабрикой
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/login.fxml")
            );
            loader.setControllerFactory(controllerFactory);

            Parent root = loader.load();
            LoginController loginController = loader.getController();
            // Передаём Stage, чтобы контроллер мог открывать главное окно
            loginController.setStage(primaryStage);

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Login – Levare Hultic Operations");
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
