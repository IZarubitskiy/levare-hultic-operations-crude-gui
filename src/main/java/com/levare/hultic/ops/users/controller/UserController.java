package com.levare.hultic.ops.users.controller;

import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.users.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * JavaFX controller for managing User (Employee) entities.
 */
public class UserController {

    private UserService userService;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Long> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> positionColumn;

    @FXML private TextField nameField;
    @FXML private TextField positionField;

    @FXML private Button createButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    public void setUserService(UserService service) {
        this.userService = service;
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleLongProperty(c.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        positionColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPosition()));

        refreshTable();

        createButton.setOnAction(e -> handleCreate());
        updateButton.setOnAction(e -> handleUpdate());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> clearForm());

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                fillForm(newSel);
            }
        });
    }

    private void refreshTable() {
        List<User> users = userService.getAll();
        userTable.setItems(FXCollections.observableArrayList(users));
    }

    private void handleCreate() {
        String name = nameField.getText().trim();
        String position = positionField.getText().trim();

        if (name.isEmpty() || position.isEmpty()) {
            showAlert("Validation Error", "Name and Position are required.");
            return;
        }

        User user = new User(null, name, position);
        userService.create(user);
        refreshTable();
        clearForm();
    }

    private void handleUpdate() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No selection", "Please select a user to update.");
            return;
        }

        selected.setName(nameField.getText().trim());
        selected.setPosition(positionField.getText().trim());
        userService.update(selected);
        refreshTable();
    }

    private void handleDelete() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No selection", "Please select a user to delete.");
            return;
        }

        userService.delete(selected.getId());
        refreshTable();
        clearForm();
    }

    private void clearForm() {
        nameField.clear();
        positionField.clear();
        userTable.getSelectionModel().clearSelection();
    }

    private void fillForm(User user) {
        nameField.setText(user.getName());
        positionField.setText(user.getPosition());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
