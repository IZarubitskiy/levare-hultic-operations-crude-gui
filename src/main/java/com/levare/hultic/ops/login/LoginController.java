package com.levare.hultic.ops.login;

import com.levare.hultic.ops.main.MainController;
import com.levare.hultic.ops.users.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for fixed login credentials (e.g. hardcoded admin).
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private TextField positionField;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void handleLogin() {
        String name = usernameField.getText().trim();
        String password = positionField.getText().trim(); // условно как пароль

        if (name.equals("1") && password.equals("1")) {
            User fixedUser = new User();
            fixedUser.setId(0L);
            fixedUser.setName("Admin");
            fixedUser.setPosition("Administrator");

            openMainWindow(fixedUser);
        } else {
            showAlert("Access Denied", "Invalid login or password.");
        }
    }

    private void openMainWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Scene mainScene = new Scene(loader.load());

            Object ctrl = loader.getController();
            if (ctrl instanceof MainController mainController) {
                mainController.setCurrentUser(user);
            }

            stage.setScene(mainScene);
            stage.setTitle("Levare Hultic - " + user.getName());
            stage.show();
            stage.setMaximized(true);

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
