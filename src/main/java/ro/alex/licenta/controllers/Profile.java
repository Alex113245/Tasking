package ro.alex.licenta.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.json.simple.JSONObject;
import ro.alex.licenta.App;

import java.net.URL;
import java.util.ResourceBundle;

public class Profile implements Initializable {
    @FXML public Label usernameLabel;
    @FXML public Label rankLabel;
    @FXML public Label tokensLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        JSONObject myPlayer = App.getInstance().getMyPlayer();
        usernameLabel.setText("Username: " + myPlayer.get("username"));
        rankLabel.setText("Rank: " + myPlayer.get("rank"));
        tokensLabel.setText("Tokens: " + myPlayer.get("tokens"));
    }
}
