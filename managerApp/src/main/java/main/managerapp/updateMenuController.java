package main.managerapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.managerapp.dbConnections.dbConnections;

import java.io.IOException;
import java.util.HashMap;

public class updateMenuController {

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField priceField;
    @FXML
    private Button submitButton;

    private dbConnections db;

    public void submitHandler(MouseEvent e) {
        idField.getText();
        HashMap<String, String> fields =  new HashMap<String, String>();


        if(!nameField.getText().isEmpty()){
            fields.put("menu_name", nameField.getText());
        }
        if(!priceField.getText().isEmpty()){
            fields.put("price", priceField.getText());
        }
        if(!descriptionField.getText().isEmpty()){
            fields.put("menu_description", descriptionField.getText());
        }

        db.updateIndividual("menu", fields, "menu_id", idField.getText());

        db.closeConnection();

        FXMLLoader fxmlLoader= new FXMLLoader(MainController.class.getResource("View.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("ABC");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void initialize(){
        db = new dbConnections();

        submitButton.setOnMouseClicked(mouseEvent -> submitHandler(mouseEvent));

    }
}
