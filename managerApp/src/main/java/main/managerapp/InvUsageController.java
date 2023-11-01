package main.managerapp;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.managerapp.dbConnections.dbConnections;
import java.util.ArrayList;

public class InvUsageController {

    public class InvResultData {
        private FloatProperty  quantity;
        private StringProperty  name;

        InvResultData(String nameIn, Float quan) {
            name = nameProperty();
            name.set(nameIn);
            quantity = quantityProperty();
            quantity.set(quan.floatValue());
        }
        public float getQuantity() {
            return quantity.get();
        }
        public FloatProperty quantityProperty() {
            if (quantity == null) quantity = new SimpleFloatProperty(this, "quantity");
            return quantity;
        }
        public void setName(String val) {
            name.set(val);
        }
        public String getName() {
            return name.get();
        }
        public StringProperty nameProperty() {
            if (name == null ) name = new SimpleStringProperty(this, "name");
            return name;
        }
    }
    @FXML
    private AnchorPane invUsgAP;
    @FXML
    private TextField startDateField;
    @FXML
    private TextField endDateField;
    @FXML
    private Button submitButton;
    @FXML
    private TableView<InvResultData> resultTable;
    @FXML
    private TableColumn<InvResultData, String> nameColumn;
    @FXML
    private TableColumn<InvResultData, Float> quantityCol;

    private dbConnections db;

    public class resultData {
        private FloatProperty  quantity;
        private StringProperty  name;
        public float getQuantity() {
            return quantity.get();
        }
        public FloatProperty quantityProperty() {
            if (quantity == null) quantity = new SimpleFloatProperty(this, "quantity");
            return quantity;
        }
        public void setName(String val) {
            name.set(val);
        }
        public String getName() {
            return name.get();
        }
        public StringProperty nameProperty() {
            if (name == null ) name = new SimpleStringProperty(this, "name");
            return name;
        }
    }
    public void submitHandler(MouseEvent e) {
        ArrayList<Pair<String, Float>> dbResults;
        Stage stage = (Stage) invUsgAP.getScene().getWindow();
        stage.setOnCloseRequest( ev ->  db.closeConnection());
        if (resultTable.getItems().size() > 0) {
            resultTable.getItems().clear();
        }
        if(!startDateField.getText().isEmpty() && !endDateField.getText().isEmpty()) {
            dbResults = db.getQuantityUsed(startDateField.getText(), endDateField.getText());
            System.out.println("Finished the inventory work !");
            quantityCol.setCellValueFactory(new PropertyValueFactory<InvResultData, Float>("quantity"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<InvResultData, String>("name"));

            for (Pair<String, Float> ele : dbResults) {
                TableRow<InvResultData> row = new TableRow<>();
                row.setItem(new InvResultData(ele.getKey(), ele.getValue()));
                resultTable.getItems().add(new InvResultData(ele.getKey(), ele.getValue()));
            }
        }
        else {
           // TODO add error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("The Inventory Usage does not return any results");
            alert.setContentText("Check your start and end date");
            alert.showAndWait();
        }
    }



    public void initialize(){
        db = new dbConnections();
        submitButton.setOnMouseClicked(mouseEvent -> submitHandler(mouseEvent));
    }
}

