package ro.alex.licenta.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import ro.alex.licenta.App;

import java.util.Objects;
import java.util.logging.Logger;

public class Notification {
    private static final Logger logger = Logger.getLogger("| Notification | ");

    public static void showErrorNotification(String s) {
        createNotification(Alert.AlertType.ERROR, "Error", "An Error has occurred.", s).showAndWait();
    }

    public static void showConfirmationNotification(String headerText, String contentText) {
        createNotification(Alert.AlertType.CONFIRMATION, "Confirmation", headerText, contentText).showAndWait();
    }

    public static void showConfirmationNotificationWithCode(String headerText, String contentText, Runnable runnable) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, headerText, ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Confirmation");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(App.class.getResource("style/main.css")).toExternalForm());
        dialogPane.getStyleClass().add("notifications");

        alert.showAndWait();

        ButtonType type = alert.getResult();
        if(!type.getButtonData().isCancelButton())
            runnable.run();
    }

    private static Alert createNotification(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(App.class.getResource("style/main.css")).toExternalForm());
        dialogPane.getStyleClass().add("notifications");
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);
        logger.info("Showing alert" + contentText);
        return alert;
    }

}
