module main.employee {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires com.jfoenix;

    exports main.employee to com.jfoenix, javafx.fxml, javafx.graphics;
    opens main.employee to javafx.fxml;
    opens main.employee.dbConnections to javafx.fxml;
}