module ro.alex.licenta {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.junit.jupiter.api;
    requires com.azure.cosmos;
    requires json.simple;
    requires java.logging;

    opens ro.alex.licenta.controllers;
    exports ro.alex.licenta;
}