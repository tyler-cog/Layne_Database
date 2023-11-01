package main.managerapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.managerapp.dbConnections.dbConnections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class ImpTenderController {

    @FXML
    private TextField menuDescField;

    @FXML
    private TextField menuIDField;

    @FXML
    private TextField menuNameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField categoryField;

    @FXML
    private Button submitButton;

    @FXML
    private Button addFoodButton;

    private dbConnections db;

    public void submitHandler(MouseEvent e) {

        HashMap<String, String> menu_fields = new HashMap<String, String>();
        menu_fields.put("menu_description", menuDescField.getText());
        menu_fields.put("menu_id", menuIDField.getText());
        menu_fields.put("menu_name", menuNameField.getText());
        menu_fields.put("price", priceField.getText());
        menu_fields.put("category", categoryField.getText());

        ArrayList<String> nothing = new ArrayList<String>();
        ArrayList<HashMap<String, String>> menu_table = new ArrayList<HashMap<String, String>>();
        menu_table.add(menu_fields);
        db.insertData("menu", menu_table, nothing);

        db.closeConnection();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Successfully added new menu item");
        alert.setHeaderText("Successfully added new menu item");
        alert.setContentText("Successfully added new menu item");
        alert.showAndWait();

    }

    public void impAddFoodHandler(MouseEvent e){
        FXMLLoader fxmlLoader= new FXMLLoader(MainController.class.getResource("ImpAddFood.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Add Menu");
        stage.setScene(new Scene(root1));
        stage.show();
    }
    public void initialize() {
        db = new dbConnections();

        submitButton.setOnMouseClicked(mouseEvent -> submitHandler(mouseEvent));
        addFoodButton.setOnMouseClicked(mouseEvent -> impAddFoodHandler(mouseEvent));

    }
}

