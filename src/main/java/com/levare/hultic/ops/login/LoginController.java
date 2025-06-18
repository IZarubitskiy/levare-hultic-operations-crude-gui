package com.levare.hultic.ops.login;

import com.levare.hultic.ops.common.AppControllerFactory;
import com.levare.hultic.ops.main.MainController;
import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.users.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;

    private Stage stage;
    private final UserService userService;
    private final Callback<Class<?>, Object> controllerFactory;

    /**
     * Конструктор вызывается AppControllerFactory, он подставляет UserService и саму фабрику контроллеров.
     */
    public LoginController(UserService userService,
                           Callback<Class<?>, Object> controllerFactory) {
        this.userService = userService;
        this.controllerFactory = controllerFactory;
    }

    /** Сеттер Stage, вызывается в Application сразу после загрузки FXML. */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Простая аутентификация: ищем пользователя по имени (пароль пока не хранится)
        User user = userService.getAll().stream()
                .filter(u -> u.getName().equals(username))
                .findFirst()
                .orElse(null);

        // Если не найден, даём демо-админа по фиксированным данным
        if (user == null && "1".equals(username) && "1".equals(password)) {
            user = new User();
            user.setId(0L);
            user.setName("Admin");
            user.setPosition("Administrator");
            userService.create(user);
        }

        if (user != null) {
            // Устанавливаем текущего пользователя в фабрике, затем открываем главное окно
            ((AppControllerFactory) controllerFactory).setCurrentUser(user);
            openMainWindow(user);
        } else {
            showAlert("Access Denied", "Invalid login or password.");
        }
    }

    private void openMainWindow(User user) {
        try {
            // 1) сообщаем фабрике, кто залогинен
            ((AppControllerFactory)controllerFactory).setCurrentUser(user);

            // 2) грузим main.fxml через тот же factory
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();

            // 3) инжектим оставшиеся параметры
            MainController mainController = loader.getController();
            mainController.setStage(stage);
            mainController.setCurrentUser(user);

            // 4) теперь заголовок уже не упадёт
            stage.setScene(new Scene(root));
            stage.setTitle("Levare Hultic – " + user.getName());
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load main window.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
