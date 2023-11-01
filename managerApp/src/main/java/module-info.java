module main.managerapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires validatorfx;
    requires com.jfoenix;

    exports main.managerapp;
    opens main.managerapp to javafx.fxml;
}