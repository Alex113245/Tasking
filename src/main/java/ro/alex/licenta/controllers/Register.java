package ro.alex.licenta.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ro.alex.licenta.App;
import ro.alex.licenta.models.Player;
import ro.alex.licenta.database.PlayerDB;
import ro.alex.licenta.util.Notification;

import java.util.logging.Logger;

public class Register {
    private static final Logger logger = Logger.getLogger("| Register | ");
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void register(ActionEvent actionEvent) {
        Player player = new Player(usernameField.getText(), emailField.getText(), passwordField.getText());

        PlayerDB playerDB = new PlayerDB();
        boolean res;

        try {
            res = playerDB.register(player);
        } catch (Exception e) {
            Notification.showErrorNotification(e.getMessage());
            return;
        }

        if(!res) {
            Notification.showErrorNotification("Email is already in use.");
            emailField.clear();
            usernameField.clear();
            passwordField.clear();
        }
        else{
            Notification.showConfirmationNotification("Register confirmation", "Your account has been successfully created!");
            App.getInstance().loadScene("profile");
        }
    }

    @FXML
    private void back(ActionEvent actionEvent) {
        App.getInstance().loadScene("welcome");
    }
}
