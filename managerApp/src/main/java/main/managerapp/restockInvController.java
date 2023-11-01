package main.managerapp;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.managerapp.dbConnections.dbConnections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class restockInvController {

    @FXML
    TableView mainTable;

    @FXML
    HBox inputBox;

    @FXML
    JFXButton updateInventoryButton;

    @FXML
    JFXButton restockButton;

    @FXML
    JFXButton previewButton;

    @FXML
    JFXButton commitButton;



    private dbConnections db;
    private ArrayList<String> colList = new ArrayList<String>(Arrays.asList("sku", "description", "quantity", "price", "quantity_per_order", "usage_category", "fill_level"));
    private invEntry currentEntry = null;
    private HashSet<invEntry> updatedEntries = new HashSet<invEntry>();
    private HashSet<invEntry> removedEntries = new HashSet<invEntry>();
    private HashSet<invEntry> addedEntries = new HashSet<>();
    ObservableList<invEntry> invEntries;


    public void restockHanlder(MouseEvent e){
        String query = """
                UPDATE inventory_items
                SET quantity = inventory_items.fill_level
                WHERE quantity < inventory_items.fill_level
                """;
        db.customQuery(query, false);

        for(invEntry i : invEntries){
            if(i.getNeed_to_order() != 0){
                i.setQuantity(i.getFill_level());
            }
        }
    }


    public void previewHandler(MouseEvent e){
        ArrayList<invEntry> sendData = new ArrayList<>();
        for(invEntry i: invEntries){
            if(i.getNeed_to_order() > 0){
                sendData.add(i);
            }
        }



        FXMLLoader fxmlLoader= new FXMLLoader(MainController.class.getResource("restockReport.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setUserData(sendData);
        restockReportController controller = (restockReportController) fxmlLoader.getController();
        controller.setStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Restock Report");
        stage.setScene(new Scene(root1));
        stage.show();
    }

    public void commitHandler(MouseEvent e){
        ArrayList<HashMap<String, String>> sendingData = new ArrayList<>();
        ArrayList<String> empty = new ArrayList<>();
        if(addedEntries.size() > 0){
            for(invEntry entry : addedEntries){
                HashMap<String,String> oneRow = new HashMap<>();
                oneRow.put("sku", entry.getSku());
                oneRow.put("description", entry.getDescription());
                oneRow.put("quantity", String.valueOf(entry.getQuantity()));
                oneRow.put("price", String.valueOf(entry.getPrice()));
                oneRow.put("quantity_per_order", entry.getQuantity_per_order());
                oneRow.put("usage_category", entry.getUsage_category());
                oneRow.put("fill_level", String.valueOf(entry.getFill_level()));
                sendingData.add(oneRow);
            }
            db.insertData("inventory_items",sendingData, empty);
        }
        if(updatedEntries.size() > 0){
            for(invEntry entry : updatedEntries){
                HashMap<String,String> oneRow = new HashMap<>();
                oneRow.put("sku", entry.getSku());
                oneRow.put("description", entry.getDescription());
                oneRow.put("quantity", String.valueOf(entry.getQuantity()));
                oneRow.put("price", String.valueOf(entry.getPrice()));
                oneRow.put("quantity_per_order", entry.getQuantity_per_order());
                oneRow.put("usage_category", entry.getUsage_category());
                oneRow.put("fill_level", String.valueOf(entry.getFill_level()));
                db.updateIndividual("inventory_items", oneRow, "sku", entry.getSku());
            }
        }
        if(removedEntries.size() > 0){
            StringBuilder query =  new StringBuilder("DELETE FROM inventory_items\n WHERE ");

            for(invEntry entry : removedEntries){
                query.append("sku = ").append("'").append(entry.getSku()).append("'").append(",\n");
            }
            query.delete(query.length()-2, query.length()-1);
            query.append(";");
            db.customQuery(query.toString(), false);
        }
        addedEntries.clear();
        updatedEntries.clear();
        removedEntries.clear();
    }

    public void updateInventoryHandler(MouseEvent e){
        String updateQuery = """
                UPDATE inventory_items ii
                SET quantity = ii.quantity - D.qRemove
                FROM
                    (SELECT fd.sku, SUM(fd.quantity_used_per_food*t1.total_use) as qRemove
                    FROM
                        (SELECT mfl.food_id, SUM(amountLink.amountOrder * mfl.quantity) as total_use
                        FROM
                            (SELECT olink.menu_id ,COUNT(olink.menu_id) as amountOrder
                            FROM (
                                SELECT order_id
                                FROM "order"
                                WHERE added_inventory = false) orderT,
                                order_menu_link olink
                                
                            WHERE orderT.order_id = olink.order_id
                            GROUP BY olink.menu_id) amountLink,
                            menu_food_link mfl
                            WHERE amountLink.menu_id = mfl.menu_id
                            GROUP BY mfl.food_id) t1,
                        food_inventory fd
                                
                    WHERE fd.food_id = t1.food_id
                    GROUP BY fd.sku)D
                WHERE D.sku = ii.sku
                RETURNING ii.sku, ii.quantity;
                """;
        ArrayList<HashMap<String, String>> changes = db.customQuery(updateQuery);
        HashMap<String, String> skuQuant = new HashMap<>();
        for(HashMap<String, String> entry : changes){
            skuQuant.put(entry.get("sku"), entry.get("quantity"));
        }

        for(invEntry entry : invEntries){
            if(skuQuant.containsKey(entry.getSku())){
                entry.setQuantity(Double.parseDouble(skuQuant.get(entry.getSku())));
            }
        }
        updateQuery = """
                update "order"
                SET added_inventory = true
                WHERE added_inventory = false;
                """;
        db.customQuery(updateQuery, false);
    }


    public void removeHandler(MouseEvent e){
        mainTable.getSelectionModel().clearSelection();
        invEntries.remove(currentEntry);
        removedEntries.add(currentEntry);
        currentEntry = null;
        JFXTextField skuField = (JFXTextField) inputBox.lookup("#skuInput");
        JFXTextField descriptionField = (JFXTextField) inputBox.lookup("#descriptionInput");
        JFXTextField quantityField = (JFXTextField) inputBox.lookup("#quantityInput");
        JFXTextField priceField = (JFXTextField) inputBox.lookup("#priceInput");
        JFXTextField quantity_per_orderField = (JFXTextField) inputBox.lookup("#quantity_per_orderInput");
        JFXTextField usage_categoryField = (JFXTextField) inputBox.lookup("#usage_categoryInput");
        JFXTextField fill_levelField = (JFXTextField) inputBox.lookup("#fill_levelInput");
        skuField.clear();
        descriptionField.clear();
        quantityField.clear();
        priceField.clear();
        quantity_per_orderField.clear();
        usage_categoryField.clear();
        fill_levelField.clear();
    }

    public void addHandler(MouseEvent e){
        JFXTextField skuField = (JFXTextField) inputBox.lookup("#skuInput");
        JFXTextField descriptionField = (JFXTextField) inputBox.lookup("#descriptionInput");
        JFXTextField quantityField = (JFXTextField) inputBox.lookup("#quantityInput");
        JFXTextField priceField = (JFXTextField) inputBox.lookup("#priceInput");
        JFXTextField quantity_per_orderField = (JFXTextField) inputBox.lookup("#quantity_per_orderInput");
        JFXTextField usage_categoryField = (JFXTextField) inputBox.lookup("#usage_categoryInput");
        JFXTextField fill_levelField = (JFXTextField) inputBox.lookup("#fill_levelInput");

        currentEntry = new invEntry(skuField.getText(), descriptionField.getText() ,quantityField.getText(), priceField.getText(),
                quantity_per_orderField.getText(), usage_categoryField.getText(), fill_levelField.getText());

        invEntries.add(currentEntry);
        mainTable.getSelectionModel().select(currentEntry);
        addedEntries.add(currentEntry);
        currentEntry = null;
        skuField.clear();
        descriptionField.clear();
        quantityField.clear();
        priceField.clear();
        quantity_per_orderField.clear();
        usage_categoryField.clear();
        fill_levelField.clear();
    }

    public void updateHandler(MouseEvent e){
        JFXTextField skuField = (JFXTextField) inputBox.lookup("#skuInput");
        JFXTextField descriptionField = (JFXTextField) inputBox.lookup("#descriptionInput");
        JFXTextField quantityField = (JFXTextField) inputBox.lookup("#quantityInput");
        JFXTextField priceField = (JFXTextField) inputBox.lookup("#priceInput");
        JFXTextField quantity_per_orderField = (JFXTextField) inputBox.lookup("#quantity_per_orderInput");
        JFXTextField usage_categoryField = (JFXTextField) inputBox.lookup("#usage_categoryInput");
        JFXTextField fill_levelField = (JFXTextField) inputBox.lookup("#fill_levelInput");

        currentEntry.setSku(skuField.getText());
        currentEntry.setDescription(descriptionField.getText());
        currentEntry.setQuantity(Double.parseDouble(quantityField.getText()));
        currentEntry.setPrice(priceField.getText());
        currentEntry.setQuantity_per_order(quantity_per_orderField.getText());
        currentEntry.setUsage_category(usage_categoryField.getText());
        currentEntry.setFill_level(Double.parseDouble(fill_levelField.getText()));

        skuField.clear();
        descriptionField.clear();
        quantityField.clear();
        priceField.clear();
        quantity_per_orderField.clear();
        usage_categoryField.clear();
        fill_levelField.clear();
        mainTable.getSelectionModel().clearSelection();
        updatedEntries.add(currentEntry);
        currentEntry = null;
    }

    public void changedSelection(invEntry entry){
        currentEntry = entry;
        JFXTextField skuField = (JFXTextField) inputBox.lookup("#skuInput");
        JFXTextField descriptionField = (JFXTextField) inputBox.lookup("#descriptionInput");
        JFXTextField quantityField = (JFXTextField) inputBox.lookup("#quantityInput");
        JFXTextField priceField = (JFXTextField) inputBox.lookup("#priceInput");
        JFXTextField quantity_per_orderField = (JFXTextField) inputBox.lookup("#quantity_per_orderInput");
        JFXTextField usage_categoryField = (JFXTextField) inputBox.lookup("#usage_categoryInput");
        JFXTextField fill_levelField = (JFXTextField) inputBox.lookup("#fill_levelInput");
        skuField.setText(entry.getSku());
        descriptionField.setText(entry.getDescription());
        quantityField.setText(String.valueOf(entry.getQuantity()));
        priceField.setText(entry.getPrice());
        quantity_per_orderField.setText(entry.getQuantity_per_order());
        usage_categoryField.setText(entry.getUsage_category());
        fill_levelField.setText(String.valueOf(entry.getFill_level()));
    }


    public void initialize(){
        db = new dbConnections();

        ArrayList<HashMap<String, String>> data = db.getColumns("inventory_items",
                colList);

        for(String item : colList){
            TableColumn col = new TableColumn(item);
            col.setCellValueFactory(new PropertyValueFactory<invEntry, String>(item));
            mainTable.getColumns().add(col);
        }
        TableColumn col = new TableColumn("need_to_order");
        col.setCellValueFactory(new PropertyValueFactory<invEntry, String>("need_to_order"));
        mainTable.getColumns().add(col);
        ArrayList<invEntry> invEntries1 = new ArrayList<>();

        for(HashMap<String, String> entry : data){
            invEntries1.add(new invEntry(entry.get("sku"), entry.get("description") , entry.get("quantity"), entry.get("price"), entry.get("quantity_per_order"),
                    entry.get("usage_category"), entry.get("fill_level")));
        }
        invEntries = FXCollections.observableArrayList(invEntries1);
        mainTable.setItems(invEntries);
        mainTable.setEditable(true);
        mainTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                if(change.getList().size() > 0){
                    changedSelection((invEntry) change.getList().get(0));
                }
            }
        });

        inputBox.spacingProperty().set(10);
        for(String item : colList){
            VBox entryBox = new VBox();
            entryBox.setAlignment(Pos.CENTER);
            Label entryLabel = new Label(item);
            JFXTextField entryField = new JFXTextField();
            inputBox.getChildren().add(entryBox);
            entryField.setId(item+"Input");
            entryBox.getChildren().addAll(entryLabel, entryField);
        }
        VBox submitBox = new VBox();
        JFXButton updateButton = new JFXButton("Update");
        JFXButton addButton = new JFXButton("Add");
        JFXButton removeButton = new JFXButton("Remove");
        submitBox.getChildren().addAll(updateButton, addButton, removeButton);
        submitBox.setAlignment(Pos.CENTER);
        submitBox.setMaxWidth(Double.MAX_VALUE);
        inputBox.getChildren().add(submitBox);
        inputBox.setPadding(new Insets(0, 10, 10, 10));
        updateButton.setOnMouseClicked(mouseEvent -> updateHandler(mouseEvent));
        addButton.setOnMouseClicked(mouseEvent -> addHandler(mouseEvent));
        removeButton.setOnMouseClicked(mouseEvent -> removeHandler(mouseEvent));
        updateInventoryButton.setOnMouseClicked(mouseEvent -> updateInventoryHandler(mouseEvent));
        commitButton.setOnMouseClicked(mouseEvent -> commitHandler(mouseEvent));
        previewButton.setOnMouseClicked(mouseEvent -> previewHandler(mouseEvent));
        restockButton.setOnMouseClicked(mouseEvent -> restockHanlder(mouseEvent));
    }




    public static class invEntry{
        public invEntry(String sku,String description, String quantity, String price, String quantity_per_order, String usage_category, String fill_level) {
            this.sku = new SimpleStringProperty(sku);
            this.description = new SimpleStringProperty(description);
            this.quantity = new SimpleDoubleProperty(Double.parseDouble(quantity));
            this.price = new SimpleStringProperty(price);
            this.quantity_per_order = new SimpleStringProperty(quantity_per_order);
            this.usage_category = new SimpleStringProperty(usage_category);
            this.fill_level = new SimpleDoubleProperty(Double.parseDouble(fill_level));
            this.need_to_order = new SimpleDoubleProperty(((this.fill_level.get() - this.getQuantity()) < 0)  ? 0 : (this.fill_level.get() - this.getQuantity()));
        }

        public String getSku() {
            return sku.get();
        }

        public SimpleStringProperty skuProperty() {
            return sku;
        }

        private final SimpleStringProperty sku;
        private final SimpleDoubleProperty quantity;
        private final SimpleStringProperty price;
        private final SimpleStringProperty quantity_per_order;

        public String getDescription() {
            return description.get();
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }

        public void setDescription(String description) {
            this.description.set(description);
        }

        private final SimpleStringProperty description;

        public void setSku(String sku) {
            this.sku.set(sku);
        }

        public void setQuantity(double quantity) {
            this.quantity.set(quantity);
            this.need_to_order.set(((this.fill_level.get() - this.getQuantity()) < 0)  ? 0 : (this.fill_level.get() - this.getQuantity()));
        }

        public void setPrice(String price) {
            this.price.set(price);
        }

        public void setQuantity_per_order(String quantity_per_order) {
            this.quantity_per_order.set(quantity_per_order);
        }

        public void setUsage_category(String usage_category) {
            this.usage_category.set(usage_category);
        }

        public void setFill_level(double fill_level) {
            this.fill_level.set(fill_level);
            this.need_to_order.set(((this.fill_level.get() - this.getQuantity()) < 0)  ? 0 : (this.fill_level.get() - this.getQuantity()));
        }

        private final SimpleStringProperty usage_category;
        private final SimpleDoubleProperty fill_level;

        public double getNeed_to_order() {
            return need_to_order.get();
        }

        public SimpleDoubleProperty need_to_orderProperty() {
            return need_to_order;
        }

        public void setNeed_to_order(double need_to_order) {
            this.need_to_order.set(need_to_order);
        }

        private final SimpleDoubleProperty need_to_order;

        public invEntry(SimpleStringProperty sku, SimpleStringProperty description , SimpleDoubleProperty quantity, SimpleStringProperty price, SimpleStringProperty quantity_per_order, SimpleStringProperty usage_category, SimpleDoubleProperty fill_level) {
            this.sku = sku;
            this.description = description;
            this.quantity = quantity;
            this.price = price;
            this.quantity_per_order = quantity_per_order;
            this.usage_category = usage_category;
            this.fill_level = fill_level;
            this.need_to_order = new SimpleDoubleProperty(((this.fill_level.get() - this.getQuantity()) < 0)  ? 0 : (this.fill_level.get() - this.getQuantity()) );
        }


        public String getUsage_category() {
            return usage_category.get();
        }

        public SimpleStringProperty usage_categoryProperty() {
            return usage_category;
        }

        public String getQuantity_per_order() {
            return quantity_per_order.get();
        }

        public SimpleStringProperty quantity_per_orderProperty() {
            return quantity_per_order;
        }

        public double getFill_level() {
            return fill_level.get();
        }

        public SimpleDoubleProperty fill_levelProperty() {
            return fill_level;
        }

        public String getPrice() {
            return price.get();
        }

        public SimpleStringProperty priceProperty() {
            return price;
        }

        public double getQuantity() {
            return quantity.get();
        }

        public SimpleDoubleProperty quantityProperty() {
            return quantity;
        }
    }
}

