package ro.alex.licenta.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ro.alex.licenta.database.QuestDB;
import ro.alex.licenta.App;
import ro.alex.licenta.models.Quest;
import ro.alex.licenta.util.Notification;

public class CreateQuest {
    @FXML public TextField nameField;
    @FXML public TextArea descriptionField;
    @FXML public TextField tokenField;

    @FXML
    public void createQuest(ActionEvent actionEvent) {
        Quest newQuest;
        try{
            newQuest = new Quest(nameField.getText(), descriptionField.getText(), (String) App.getInstance().getMyPlayer().get("id"), Integer.parseInt(tokenField.getText()));
        } catch(NumberFormatException e){
            Notification.showErrorNotification("Please introduce a number into the tokens field.");
            return;
        }

        QuestDB questDB = new QuestDB();
        boolean res;

        try {
            res = questDB.createQuest(newQuest);
        }catch(Exception e){
            Notification.showErrorNotification(e.getMessage());
            return;
        }

        if(res){
            Notification.showConfirmationNotification("Task confirmation", "Task created successfully");
            App.getInstance().closeWindow();
        }
    }
}
