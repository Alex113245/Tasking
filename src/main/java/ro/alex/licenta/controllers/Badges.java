package ro.alex.licenta.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import ro.alex.licenta.App;
import ro.alex.licenta.models.JSON;
import ro.alex.licenta.models.Player;

import java.net.URL;
import java.util.ResourceBundle;

public class Badges implements Initializable {
    @FXML public ScrollPane scrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Player myPlayer = JSON.deserializeJSONPlayer(App.getInstance().getMyPlayer());

        VBox primaryVBox = new VBox();
        primaryVBox.setPadding(new Insets(20, 15, 20, 15));
        primaryVBox.getStyleClass().add("background-vbox-scroll-pane");
        primaryVBox.setSpacing(30);

        Label badgesLabel = new Label("Badges");
        badgesLabel.getStyleClass().add("subtitle");
        badgesLabel.setAlignment(Pos.TOP_CENTER);
        primaryVBox.setAlignment(Pos.TOP_CENTER);
        primaryVBox.getChildren().add(badgesLabel);
        primaryVBox.setAlignment(Pos.TOP_LEFT);

        if(myPlayer.getBadges() != null) {
            for (var badge : myPlayer.getBadges()) {
                String completedBadge = "You have completed ";
                String completedTakenBadge = " taken task";

                String completedCreatedBadge = " created task";

                VBox badgeVBox = new VBox();
                badgeVBox.getStyleClass().add("background-badge");
                badgeVBox.setPadding(new Insets(0, 10, 0, 10));
                Image image = new Image(App.class.getResource("badges/" + badge).toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                badgeVBox.setAlignment(Pos.CENTER);
                badgeVBox.getChildren().add(imageView);

                Label descriptionLabel = new Label();
                descriptionLabel.getStyleClass().add("label-badge");
                String code = "";

                if (badge.contains("1")) code = "1";
                if (badge.contains("2")) code = "5";
                if (badge.contains("3")) code = "10";
                if (badge.contains("4")) code = "15";
                if (badge.contains("5")) code = "20";

                if (badge.contains("Taken") && badge.contains("1"))
                    descriptionLabel.setText(completedBadge + code + completedTakenBadge);
                if (badge.contains("Taken") && !badge.contains("1"))
                    descriptionLabel.setText(completedBadge + code + completedTakenBadge + "s");
                if (badge.contains("Created") && badge.contains("1"))
                    descriptionLabel.setText(completedBadge + code + completedCreatedBadge);
                if (badge.contains("Created") && !badge.contains("1"))
                    descriptionLabel.setText(completedBadge + code + completedCreatedBadge + "s");

                descriptionLabel.setTextAlignment(TextAlignment.CENTER);
                descriptionLabel.setAlignment(Pos.CENTER);
                badgeVBox.getChildren().add(descriptionLabel);

                primaryVBox.getChildren().add(badgeVBox);
            }
        }

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        primaryVBox.setPrefHeight(350);
        scrollPane.setContent(primaryVBox);
    }
}
