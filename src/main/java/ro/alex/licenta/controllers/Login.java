package ro.alex.licenta.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ro.alex.licenta.App;
import ro.alex.licenta.database.PlayerDB;
import ro.alex.licenta.util.Notification;

import java.util.logging.Logger;

public class Login {
    private static final Logger logger = Logger.getLogger("| Login | ");
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void login(ActionEvent actionEvent) {
        logger.info("Checking login credentials");

        PlayerDB playerDB = new PlayerDB();
        boolean res = false;
        try {
            res = playerDB.login(emailField.getText(), passwordField.getText());
        } catch (Exception e) {
            Notification.showErrorNotification(e.getMessage());
            emailField.clear();
            passwordField.clear();
            return;
        }

        Notification.showConfirmationNotification("Login Confirmation", "You have successfully logged in!");
        App.getInstance().loadScene("profile");
    }

    @FXML
    private void back(ActionEvent actionEvent) {
        App.getInstance().loadScene("welcome");
    }
}
