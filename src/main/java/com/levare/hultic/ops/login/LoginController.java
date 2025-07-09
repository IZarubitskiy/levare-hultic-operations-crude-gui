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

import java.util.List;

/**
 * Controller for the login screen: проверяет вводимые логин/пароль
 * против заранее заданного списка и загружает соответствующего пользователя из БД.
 */
public class LoginController {

    /**
     * DTO для хранения в коде логина, пароля и связанного userId
     */
    private static final class HardcodedUser {
        final String username, password;
        final Long userId;

        HardcodedUser(String u, String p, Long id) {
            this.username = u;
            this.password = p;
            this.userId = id;
        }
    }

    /**
     * Список разрешённых «жёстко прописанных» учёток
     */
    private static final List<HardcodedUser> ALLOWED = List.of(
            new HardcodedUser("1", "1", 1L),
            new HardcodedUser("Magdy", "2", 2L),
            new HardcodedUser("Rafik", "3", 3L),
            new HardcodedUser("Amr", "4", 4L)
    );

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    private Stage stage;
    private final UserService userService;
    private final Callback<Class<?>, Object> controllerFactory;

    public LoginController(UserService userService,
                           Callback<Class<?>, Object> controllerFactory) {
        this.userService = userService;
        this.controllerFactory = controllerFactory;
    }

    /**
     * Вызывается из Application сразу после загрузки FXML
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String login = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        // Ищем среди жёстко прописанных
        HardcodedUser match = ALLOWED.stream()
                .filter(h -> h.username.equals(login) && h.password.equals(pass))
                .findFirst()
                .orElse(null);

        if (match == null) {
            showAlert("Access Denied", "Invalid login or password.");
            return;
        }

        // Загружаем из базы пользователя с найденным ID
        User user = userService.getAll().stream()
                .filter(u -> u.getId() != null && u.getId().equals(match.userId))
                .findFirst()
                .orElse(null);

        if (user == null) {
            showAlert("Error", "User with ID " + match.userId + " not found in database.");
            return;
        }

        // Устанавливаем в фабрике текущего юзера и открываем главное окно
        ((AppControllerFactory) controllerFactory).setCurrentUser(user);
        openMainWindow(user);
    }

    private void openMainWindow(User user) {
        try {
            ((AppControllerFactory) controllerFactory).setCurrentUser(user);
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/main.fxml")
            );
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();

            MainController mainCtrl = loader.getController();
            mainCtrl.setStage(stage);
            mainCtrl.setCurrentUser(user);

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
