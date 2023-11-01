package main.managerapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.managerapp.dbConnections.dbConnections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class MainController {

    @FXML
    private Button addMenuButton;

    @FXML
    private Button updateMenuButton;

    @FXML
    private Button popularButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button invUsageButton;

    @FXML
    private ListView<String> menuList;

    @FXML
    private ListView<String> inventoryList;

    @FXML
    private Button restockOperations;

    private dbConnections db;



    public void addMenuHandler(MouseEvent e){
        FXMLLoader fxmlLoader= new FXMLLoader(MainController.class.getResource("ImpTender.fxml"));
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

    public void updateMenuHandler(MouseEvent e){
        FXMLLoader fxmlLoader= new FXMLLoader(MainController.class.getResource("form2.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Update Menu");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void restockOperationsHandler(MouseEvent e){
        FXMLLoader fxmlLoader= new FXMLLoader(MainController.class.getResource("restock.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Restock Operations");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void addInventoryHandler(MouseEvent e){
        FXMLLoader fxmlLoader= new FXMLLoader(MainController.class.getResource("form3.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Add Inventory");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void updateInventoryHandler(MouseEvent e){
        FXMLLoader fxmlLoader= new FXMLLoader(MainController.class.getResource("form4.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Update Inventory");
        stage.setScene(new Scene(root1));
        stage.show();
    }
//    havent edited from copy pasted updateInvHandler
    public void popularHandler(MouseEvent e){
        FXMLLoader fxmlLoader= new FXMLLoader(MainController.class.getResource("popular.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Order Popularity");
        stage.setScene(new Scene(root1));
        stage.show();
    }
    private void refreshHandler(MouseEvent mouseEvent) {
        ArrayList<String> columns = new ArrayList<String>(Arrays.asList("menu_id", "menu_name", "menu_description"));
        ArrayList<HashMap<String, String>> menuData = db.getColumns("menu", columns);

        menuList.getItems().clear();
        for(HashMap<String, String> m : menuData){
            menuList.getItems().add(m.get("menu_id") + " | " + m.get("menu_name") + " | " + m.get("menu_description"));
        }

        inventoryList.getItems().clear();
        columns = new ArrayList<String>(Arrays.asList("sku", "quantity", "category", "price"));
        ArrayList<HashMap<String, String>> inventoryData = db.getColumns("inventory_items", columns);

        for(HashMap<String, String> m : inventoryData){
            inventoryList.getItems().add(m.get("sku") + " | "+ m.get("quantity") + " | " + m.get("category") + " | " + m.get("price"));
        }
    }

    public void invUsageHandler(MouseEvent e){
        FXMLLoader fxmlLoader= new FXMLLoader(MainController.class.getResource("invUsage.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Inventory Usage");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void initialize(){
        // Start Connection with Database
        db = new dbConnections();

        ArrayList<String> columns = new ArrayList<String>(Arrays.asList("menu_id", "menu_name", "menu_description", "price"));
        ArrayList<HashMap<String, String>> menuData = db.getColumns("menu", columns);

        addMenuButton.setOnMouseClicked(mouseEvent -> addMenuHandler(mouseEvent));
        updateMenuButton.setOnMouseClicked(mouseEvent -> updateMenuHandler(mouseEvent));
        popularButton.setOnMouseClicked(mouseEvent -> popularHandler(mouseEvent));
        refreshButton.setOnMouseClicked(mouseEvent -> refreshHandler(mouseEvent));
        invUsageButton.setOnMouseClicked(mouseEvent -> invUsageHandler(mouseEvent));
        restockOperations.setOnMouseClicked(mouseEvent -> restockOperationsHandler(mouseEvent));


        for(HashMap<String, String> m : menuData){
            menuList.getItems().add(m.get("menu_id") + " | " + m.get("menu_name") + " | " + m.get("price") + " | " + m.get("menu_description"));
        }

        columns = new ArrayList<String>(Arrays.asList("sku", "quantity", "category", "price"));
        ArrayList<HashMap<String, String>> inventoryData = db.getColumns("inventory_items", columns);

        for(HashMap<String, String> m : inventoryData){
            inventoryList.getItems().add(m.get("sku") + " | "+ m.get("quantity") + " | " + m.get("category") + " | " + m.get("price"));
        }

    }



}
