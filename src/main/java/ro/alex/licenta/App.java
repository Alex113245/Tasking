package ro.alex.licenta;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import ro.alex.licenta.database.PlayerDB;
import ro.alex.licenta.util.Notification;

import java.io.IOException;
import java.util.logging.Logger;

public class App extends Application {
    private static final Logger logger = Logger.getLogger("| App | ");
    private static App appInstance;

    private Stage primaryWindow;

    private Stage createQuestWindow;
    private Scene scene;

    private JSONObject myPlayer = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Application starting");
        appInstance = this;
        this.primaryWindow = primaryStage;

        scene = new Scene(loadFXML("Welcome"));
        primaryWindow.setTitle("Task Helper");
        primaryWindow.setScene(scene);

        primaryWindow.setWidth(getScreenWidth());
        primaryWindow.setHeight(getScreenHeight());
        setDimensions();

        scene.getStylesheets().add(App.class.getResource("style/main.css").toExternalForm());

        setOnExit();
    }

    public static Parent loadFXML(String fileName) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ui/" + fileName + ".fxml"));
        logger.info("Loading " + fileName + " fxml file");
        return fxmlLoader.load();
    }

    public void setOnExit() {
        getStage().setOnCloseRequest(event -> {
            event.consume();
            softShutdown();
        });
    }

    public void hardShutdown() {
        logger.info("Shutting down!");
        Platform.exit();
        System.exit(0);
    }

    public void softShutdown() {
        Notification.showConfirmationNotificationWithCode("Closing application", "Are you sure you want to leave?", this::hardShutdown);
    }

    public void setDimensions() {
        var oldHeight = primaryWindow.getHeight();
        var oldWidth = primaryWindow.getWidth();

        primaryWindow.setWidth(oldWidth - 1.0);
        primaryWindow.setWidth(oldWidth + 1.0);
        primaryWindow.setHeight(oldHeight - 1.0);
        primaryWindow.setHeight(oldHeight + 1.0);

        primaryWindow.show();
    }

    public void loadScene(String fileName){
        try {
            Scene newScene = new Scene(App.loadFXML(fileName));
            newScene.getStylesheets().add(App.class.getResource("style/main.css").toExternalForm());
            getStage().setScene(newScene);
            setDimensions();

            if(getMyPlayer() != null)
            {
                try{
                    PlayerDB playerDB = new PlayerDB();
                    var playerJSON = playerDB.findPlayerById((String) getMyPlayer().get("id"));
                    setMyPlayer(playerJSON);
                } catch (Exception e) {
                    Notification.showErrorNotification(e.getMessage());
                }
            }
        } catch (IOException e){
            logger.info("The log in fxml file couldn't be loaded.");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }

    public static double getScreenWidth(){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        return screenBounds.getWidth();
    }

    public static double getScreenHeight(){
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        return screenBounds.getHeight();
    }

    public Stage getStage(){
        return this.primaryWindow;
    }

    public static App getInstance(){
        return appInstance;
    }

    public JSONObject getMyPlayer() {
        return myPlayer;
    }

    public void setMyPlayer(JSONObject myPlayer) {
        this.myPlayer = myPlayer;
    }

    public void openWindow(String title, String fxmlFile, double size) {
        try {
            createQuestWindow = new Stage();
            final int screenSize = (int) Math.round(App.getScreenHeight() * size);
            Parent parent = App.loadFXML(fxmlFile);
            Scene scene = new Scene(parent, screenSize, screenSize);
            createQuestWindow.setTitle(title);
            createQuestWindow.setScene(scene);
            createQuestWindow.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeWindow() {
        createQuestWindow.close();
    }
}
