package ro.alex.licenta.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import ro.alex.licenta.App;
import ro.alex.licenta.util.Notification;


public class Navigation {
    @FXML
    public void loadHome(ActionEvent actionEvent) {
        App.getInstance().loadScene("home");
    }

    @FXML
    public void myProfile(ActionEvent actionEvent) {
        App.getInstance().loadScene("profile");
    }

    @FXML
    public void logout(ActionEvent actionEvent) {
        App.getInstance().setMyPlayer(null);
        Notification.showConfirmationNotification("Logout confirmation", "Logged out successfully.");
        App.getInstance().loadScene("welcome");
    }

    @FXML
    public void leaderboard(ActionEvent actionEvent) {
        App.getInstance().loadScene("leaderboard");
    }
    @FXML
    public void help(ActionEvent actionEvent) {
        App.getInstance().openWindow("Help", "help", 0.7);
    }
}
