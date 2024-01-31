package ro.alex.licenta.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.json.simple.JSONObject;
import ro.alex.licenta.database.QuestDB;
import ro.alex.licenta.App;
import ro.alex.licenta.database.PlayerDB;
import ro.alex.licenta.util.Notification;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class IncompleteQuests implements Initializable {
    @FXML public ScrollPane scrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        QuestDB questDB = new QuestDB();
        PlayerDB playerDB = new PlayerDB();

        String myId = (String) App.getInstance().getMyPlayer().get("id");

        List<JSONObject> questsIncomplete = null;
        try {
            questsIncomplete = questDB.getIncompleteQuests(myId,5);
        } catch (Exception e) {
            Notification.showErrorNotification(e.getMessage());
        }

        if(questsIncomplete == null) return;

        VBox primaryVBox = new VBox();
        primaryVBox.setPadding(new Insets(20, 15, 20, 15));
        primaryVBox.setSpacing(30);

        Label availableQuestsLabel = new Label("Available Tasks");
        availableQuestsLabel.getStyleClass().add("title");
        availableQuestsLabel.setAlignment(Pos.TOP_CENTER);
        primaryVBox.setAlignment(Pos.TOP_CENTER);
        primaryVBox.getChildren().add(availableQuestsLabel);
        primaryVBox.setAlignment(Pos.TOP_LEFT);

        for(var quest : questsIncomplete){
            VBox questVBox = new VBox();
            questVBox.getStyleClass().add("background-single-quest");
            questVBox.setPadding(new Insets(0, 10, 5, 10));

            HBox detailsHBox = new HBox();
            detailsHBox.setSpacing(10);

            Label nameLabel = new Label((String) quest.get("name"));
            nameLabel.getStyleClass().add("label-quest");
            detailsHBox.getChildren().add(nameLabel);

            Label tokensLeftFakeLabel = new Label();
            tokensLeftFakeLabel.setMaxWidth(Integer.MAX_VALUE);
            HBox.setHgrow(tokensLeftFakeLabel, Priority.ALWAYS);
            detailsHBox.getChildren().add(tokensLeftFakeLabel);

            Label tokensLabel = new Label("Tokens: " + quest.get("tokens"));
            tokensLabel.getStyleClass().add("label-quest");
            detailsHBox.getChildren().add(tokensLabel);

            Label leftFakeLabel = new Label();
            leftFakeLabel.setMaxWidth(Integer.MAX_VALUE);
            detailsHBox.getChildren().add(leftFakeLabel);
            HBox.setHgrow(leftFakeLabel, Priority.ALWAYS);

            JSONObject playerCreator = null;
            try {
                playerCreator = playerDB.findPlayerById((String) quest.get("createdBy_id"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Label createdByLabel = new Label((String) playerCreator.get("username"));
            createdByLabel.getStyleClass().add("label-quest");

            detailsHBox.getChildren().add(createdByLabel);

            Label rightFakeLabel = new Label();
            rightFakeLabel.setMaxWidth(Integer.MAX_VALUE);
            HBox.setHgrow(rightFakeLabel, Priority.ALWAYS);
            detailsHBox.getChildren().add(rightFakeLabel);

            Button button = new Button("Accept task");
            button.getStyleClass().add("button-accept");
            button.setTextAlignment(TextAlignment.CENTER);

            button.setOnMouseClicked(actionEvent -> {
                System.out.println("Button pressed for the task" + quest.get("name"));

                try {
                    questDB.takeQuest(myId, (String) quest.get("id"));

                    Notification.showConfirmationNotification("Task accepted", "Task: " + quest.get("name") + " accepted");
                    App.getInstance().loadScene("home");
                } catch (Exception e) {
                    Notification.showErrorNotification(e.getMessage());
                }
            });

            button.setAlignment(Pos.CENTER_RIGHT);

            detailsHBox.getChildren().add(button);

            detailsHBox.getStyleClass().add("background-single-quest");
            questVBox.getChildren().add(detailsHBox);

            TextArea textAreaDescription = new TextArea((String) quest.get("description"));
            textAreaDescription.setWrapText(true);
            textAreaDescription.setEditable(false);
            textAreaDescription.getStyleClass().add("textArea-normal");

            questVBox.getChildren().add(textAreaDescription);

            primaryVBox.getChildren().add(questVBox);
        }

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        primaryVBox.setPrefHeight(350);
        scrollPane.setContent(primaryVBox);
    }
}
