package main.managerapp;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;

public class restockReportController {

    @FXML
    TableView mainTable;

    public Stage mainStage;

    public ArrayList<restockInvController.invEntry> items;
    public void setStage(Stage stage){
        mainStage = stage;
        items = (ArrayList<restockInvController.invEntry>) mainStage.getUserData();

        TableColumn col = new TableColumn("Sku");
        col.setCellValueFactory(new PropertyValueFactory<Report, String>("sku"));
        mainTable.getColumns().add(col);

        col = new TableColumn("Item");
        col.setCellValueFactory(new PropertyValueFactory<Report, String>("description"));
        mainTable.getColumns().add(col);

        col = new TableColumn("need to order");
        col.setCellValueFactory(new PropertyValueFactory<Report, String>("need_to_order"));
        mainTable.getColumns().add(col);

        col = new TableColumn("current supply");
        col.setCellValueFactory(new PropertyValueFactory<Report, String>("current_supply"));
        mainTable.getColumns().add(col);

        col = new TableColumn("units");
        col.setCellValueFactory(new PropertyValueFactory<Report, String>("units"));
        mainTable.getColumns().add(col);

        ArrayList<Report> temp = new ArrayList<>();
        for(restockInvController.invEntry i : items){
            Report row = new Report(i.skuProperty(), i.descriptionProperty(), i.need_to_orderProperty(), i.quantityProperty(), i.quantity_per_orderProperty());
            temp.add(row);
        }
        ObservableList<Report> entries = FXCollections.observableArrayList(temp);
        mainTable.setItems(entries);
    }

    public static class Report{
        private final SimpleStringProperty sku;
        private final SimpleStringProperty description;
        private final SimpleDoubleProperty need_to_order;
        private final SimpleDoubleProperty current_supply;
        private final SimpleStringProperty units;

        public Report(SimpleStringProperty sku, SimpleStringProperty description, SimpleDoubleProperty need_to_order, SimpleDoubleProperty current_supply, SimpleStringProperty units) {
            this.sku = sku;
            this.description = description;
            this.need_to_order = need_to_order;
            this.current_supply = current_supply;
            this.units = units;
        }

        public String getSku() {
            return sku.get();
        }

        public SimpleStringProperty skuProperty() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku.set(sku);
        }

        public String getDescription() {
            return description.get();
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }

        public void setDescription(String description) {
            this.description.set(description);
        }

        public double getNeed_to_order() {
            return need_to_order.get();
        }

        public SimpleDoubleProperty need_to_orderProperty() {
            return need_to_order;
        }

        public void setNeed_to_order(double need_to_order) {
            this.need_to_order.set(need_to_order);
        }

        public double getCurrent_supply() {
            return current_supply.get();
        }

        public SimpleDoubleProperty current_supplyProperty() {
            return current_supply;
        }

        public void setCurrent_supply(double current_supply) {
            this.current_supply.set(current_supply);
        }

        public String getUnits() {
            return units.get();
        }

        public SimpleStringProperty unitsProperty() {
            return units;
        }

        public void setUnits(String units) {
            this.units.set(units);
        }
    }

    public void initialize(){
    }
}
