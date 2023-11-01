package main.managerapp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.managerapp.dbConnections.dbConnections;

import java.util.ArrayList;
import java.util.HashMap;


public class ImpAddFoodController {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField foodDescField;

    @FXML
    private TextField foodIDField;

    @FXML
    private TextField foodNameField;

    @FXML
    private TextField menuIDField;

    @FXML
    private TextField entryIDField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField quanUsedField;

    @FXML
    private TextField skuField;

    @FXML
    private RadioButton addNewFood;

    @FXML
    private Button submitButton;

    private dbConnections db;

    public void submitHandler(MouseEvent e) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.setOnCloseRequest( ev ->  db.closeConnection());

        ArrayList<String> nothing = new ArrayList<String>();
        if (addNewFood.isSelected()) {
            HashMap<String, String> food_and_drink_fields = new HashMap<String, String>();
            food_and_drink_fields.put("food_description", foodDescField.getText());
            food_and_drink_fields.put("food_id", foodIDField.getText());
            food_and_drink_fields.put("food_name", foodNameField.getText());

            ArrayList<HashMap<String, String>> fd_table = new ArrayList<HashMap<String, String>>();
            fd_table.add(food_and_drink_fields);
            db.insertData("food_and_drinks", fd_table, nothing);
        }
        HashMap<String, String> food_inventory_fields = new HashMap<String, String>();
        food_inventory_fields.put("sku", skuField.getText());
        food_inventory_fields.put("food_id", foodIDField.getText());
        food_inventory_fields.put("quantity_used_per_food", quanUsedField.getText());

        ArrayList<HashMap<String, String>> fi_table = new ArrayList<HashMap<String, String>>();
        fi_table.add(food_inventory_fields);
        db.insertData("food_inventory", fi_table, nothing);

        HashMap<String, String> menu_food_link_fields = new HashMap<String, String>();
        menu_food_link_fields.put("food_id", foodIDField.getText());
        menu_food_link_fields.put("entry_id", entryIDField.getText());
        menu_food_link_fields.put("menu_id", menuIDField.getText());
        menu_food_link_fields.put("quantity", quantityField.getText());

        ArrayList<HashMap<String, String>> mfl_table = new ArrayList<HashMap<String, String>>();
        mfl_table.add(menu_food_link_fields);
        db.insertData("menu_food_link", mfl_table, nothing);

        //db.closeConnection();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Successfully added new menu item");
        alert.setHeaderText("Successfully added new menu item");
        alert.setContentText("Successfully added new menu item");
        alert.showAndWait();

    }

    public void initialize() {
        db = new dbConnections();

        submitButton.setOnMouseClicked(mouseEvent -> submitHandler(mouseEvent));

    }
}

