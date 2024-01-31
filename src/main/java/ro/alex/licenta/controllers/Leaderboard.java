package ro.alex.licenta.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.json.simple.JSONObject;
import ro.alex.licenta.App;
import ro.alex.licenta.database.PlayerDB;
import ro.alex.licenta.util.Notification;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Leaderboard implements Initializable {
    @FXML public ScrollPane scrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PlayerDB playerDB = new PlayerDB();

        List<JSONObject> topRanks = null;
        try {
            topRanks = playerDB.findTop(10);
        } catch(Exception e) {
            Notification.showErrorNotification(e.getMessage());
        }

        if(topRanks == null) return;

        VBox primaryVBox = new VBox();
        primaryVBox.setSpacing(20);

        int rank = 1;

        for(var player : topRanks) {
            VBox playerVBox = new VBox();
            playerVBox.getStyleClass().add("background-single-quest");
            playerVBox.setPadding(new Insets(0, 10, 0, 10));
            playerVBox.setSpacing(10);

            HBox playerHBox = new HBox();
            playerHBox.setPadding(new Insets(0, 10, 0, 10));

            Label posLabel = new Label(String.valueOf(rank));
            posLabel.getStyleClass().add("label-leaderboard");
            posLabel.setPrefWidth(150);
            posLabel.setAlignment(Pos.CENTER_LEFT);
            posLabel.setTextAlignment(TextAlignment.LEFT);
            playerHBox.getChildren().add(posLabel);

            Label fakeLabelLeft = new Label();
            HBox.setHgrow(fakeLabelLeft, Priority.ALWAYS);
            fakeLabelLeft.setMaxWidth(Integer.MAX_VALUE);
            playerHBox.getChildren().add(fakeLabelLeft);

            Label usernameLabel = new Label((String) player.get("username"));
            usernameLabel.getStyleClass().add("label-leaderboard");
            playerHBox.getChildren().add(usernameLabel);

            Label fakeLabelRight = new Label();
            HBox.setHgrow(fakeLabelRight, Priority.ALWAYS);
            fakeLabelRight.setMaxWidth(Integer.MAX_VALUE);
            playerHBox.getChildren().add(fakeLabelRight);

            Label rankLabel = new Label("Level: " + player.get("rank"));
            rankLabel.getStyleClass().add("label-leaderboard");
            rankLabel.setPrefWidth(150);
            playerHBox.getChildren().add(rankLabel);

            playerVBox.getChildren().add(playerHBox);

            HBox badgesHBox = new HBox();

            var badges_id = (List<String>) player.get("badges_id");

            if(badges_id != null) {
                for (var badge : badges_id) {
                    Image image = new Image(App.class.getResource("badges/" + badge).toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(75);
                    imageView.setFitWidth(75);
                    badgesHBox.getChildren().add(imageView);
                }

                badgesHBox.setAlignment(Pos.CENTER);
                badgesHBox.setSpacing(10);

                playerVBox.getChildren().add(badgesHBox);
            }

            primaryVBox.getChildren().add(playerVBox);

            rank++;
        }

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        primaryVBox.setPrefHeight(350);
        scrollPane.setContent(primaryVBox);
    }
}
