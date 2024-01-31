package ro.alex.licenta.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import ro.alex.licenta.App;

import java.net.URL;
import java.util.ResourceBundle;

public class Home implements Initializable {

    public void createQuest(ActionEvent actionEvent) {
        App.getInstance().openWindow("Create Task", "create_quest", 0.7);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
