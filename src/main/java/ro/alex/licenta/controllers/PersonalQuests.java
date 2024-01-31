package ro.alex.licenta.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.json.simple.JSONObject;
import ro.alex.licenta.database.QuestDB;
import ro.alex.licenta.App;
import ro.alex.licenta.database.PlayerDB;
import ro.alex.licenta.util.Notification;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PersonalQuests implements Initializable {
    @FXML public ScrollPane scrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        QuestDB questDB = new QuestDB();
        PlayerDB playerDB = new PlayerDB();

        String myId = (String) App.getInstance().getMyPlayer().get("id");

        List<JSONObject> createdQuests;
        List<JSONObject> acceptedQuests;
        List<JSONObject> personalQuests = new ArrayList<>();
        try {
            createdQuests = questDB.getCreatedQuests(myId);
            acceptedQuests = questDB.getAcceptedQuests(myId);

            if(createdQuests != null) personalQuests.addAll(createdQuests);
            if(acceptedQuests != null) personalQuests.addAll(acceptedQuests);
        } catch (Exception e) {
            Notification.showErrorNotification(e.getMessage());
        }



        VBox primaryVBox = new VBox();
        primaryVBox.setPadding(new Insets(20, 15, 20, 15));
        primaryVBox.setSpacing(30);

        Label personalQuestsLabel = new Label("Personal Tasks");
        personalQuestsLabel.getStyleClass().add("subtitle");
        personalQuestsLabel.setAlignment(Pos.TOP_CENTER);
        primaryVBox.setAlignment(Pos.TOP_CENTER);
        primaryVBox.getChildren().add(personalQuestsLabel);
        primaryVBox.setAlignment(Pos.TOP_LEFT);

        if(!personalQuests.isEmpty()) {
            for (var quest : personalQuests) {
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

                boolean myPlayerCreated = false;
                JSONObject otherPlayer = null;
                try {
                    if (myId.equals(quest.get("createdBy_id"))) {
                        if (quest.get("takenBy_id") != null)
                            otherPlayer = playerDB.findPlayerById((String) quest.get("takenBy_id"));
                        myPlayerCreated = true;
                    } else {
                        otherPlayer = playerDB.findPlayerById((String) quest.get("createdBy_id"));
                    }
                } catch (Exception e) {
                    Notification.showErrorNotification(e.getMessage());
                }

                if (otherPlayer != null) {
                    Label otherPlayerLabel = new Label((String) otherPlayer.get("username"));
                    otherPlayerLabel.getStyleClass().add("label-quest");
                    detailsHBox.getChildren().add(otherPlayerLabel);
                }

                Label rightFakeLabel = new Label();
                rightFakeLabel.setMaxWidth(Integer.MAX_VALUE);
                HBox.setHgrow(rightFakeLabel, Priority.ALWAYS);
                detailsHBox.getChildren().add(rightFakeLabel);

                Button button = new Button();
                if (myPlayerCreated && quest.get("takenBy_id") == null)
                    button.setText("Cancel");
                else {
                    if (myId.equals(quest.get("createdBy_id")) && !(boolean) quest.get("acceptedCreator"))
                        button.setText("Confirm");
                    else if (myId.equals(quest.get("takenBy_id")) && !(boolean) quest.get("completedTaker"))
                        button.setText("Complete");
                    else {
                        button.setDisable(true);
                        button.setVisible(false);
                        Label completedLabel = new Label("Waiting for the other person to confirm.");
                        completedLabel.getStyleClass().add("label-quest");
                        detailsHBox.getChildren().add(completedLabel);
                    }
                }


                boolean finalMyPlayerCreated = myPlayerCreated;
                button.setOnMouseClicked((actionEvent) -> {
                    System.out.println("Button pressed for the task" + quest.get("name"));

                    try {
                        if (finalMyPlayerCreated && quest.get("takenBy_id") == null) {
                            questDB.cancelQuest(myId, (String) quest.get("id"));
                            Notification.showConfirmationNotification("Task confirmation", "Task: " + quest.get("name") + " canceled");
                        } else {
                            questDB.completeQuest(myId, (String) quest.get("id"));
                            Notification.showConfirmationNotification("Task confirmation", "Task: " + quest.get("name") + " set as completed");
                        }

                        App.getInstance().loadScene("profile");
                    } catch (Exception e) {
                        Notification.showErrorNotification(e.getMessage());
                    }
                });


                button.getStyleClass().add("button-accept");
                button.setTextAlignment(TextAlignment.CENTER);

                if (!button.getText().isEmpty())
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
        }

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        primaryVBox.setPrefHeight(350);
        scrollPane.setContent(primaryVBox);
    }
}
