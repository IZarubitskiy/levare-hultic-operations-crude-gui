package com.levare.hultic.ops.main;

import com.levare.hultic.ops.users.entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the main application screen.
 */
public class MainController {

    @FXML
    private Label userLabel;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        // если userLabel уже загружен — обновим
        if (userLabel != null) {
            userLabel.setText("Logged in as: " + user.getName() + " (" + user.getPosition() + ")");
        }
    }

    @FXML
    public void initialize() {
        if (currentUser != null) {
            userLabel.setText("Logged in as: " + currentUser.getName() + " (" + currentUser.getPosition() + ")");
        }
    }
}
