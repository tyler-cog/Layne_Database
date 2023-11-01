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

public class updateInvController {

    @FXML
    private TextField skuField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField quantity_per_orderField;
    @FXML
    private TextField usage_categoryField;
    @FXML
    private Button submitButton;

    private dbConnections db;

    public void submitHandler(MouseEvent e) {
        HashMap<String, String> fields =  new HashMap<String, String>();


        if(!quantityField.getText().isEmpty()){
            fields.put("quantity", quantityField.getText());
        }
        if(!categoryField.getText().isEmpty()){
            fields.put("category", categoryField.getText());
        }
        if(!priceField.getText().isEmpty()) {
            fields.put("price", priceField.getText());
        }
        if(!descriptionField.getText().isEmpty()){
            fields.put("description", descriptionField.getText());
        }
        if(!quantity_per_orderField.getText().isEmpty()){
            fields.put("quantity_per_order", quantity_per_orderField.getText());
        }
        if(!usage_categoryField.getText().isEmpty()){
            fields.put("usage_category", usage_categoryField.getText());
        }


        db.updateIndividual("inventory_items", fields, "sku", skuField.getText());
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
