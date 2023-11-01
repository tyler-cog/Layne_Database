package main.managerapp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import main.managerapp.dbConnections.dbConnections;
import java.util.*;

public class popController{

        @FXML
        private TextField fromField;
        @FXML
        private TextField toField;
        @FXML
        private ListView<String> popList;
        @FXML
        private Button submitButton;

        private dbConnections db;

        public void submitHandler(MouseEvent e) {

                // checks if user doesnt input a date
                if(fromField.getText().isEmpty() || toField.getText().isEmpty()) {
                        // TODO add error message
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("One of the date entries is empty!");
                        alert.setContentText("Check your start and end date.");
                        alert.showAndWait();
                }

                // gets the dates from the user
                String fromDate = fromField.getText();
                String toDate = toField.getText();

                // Query to get food item names later
                ArrayList<String> menuColumn = new ArrayList<String>(Arrays.asList("menu_id", "menu_name"));
                ArrayList<HashMap<String, String>> menuData = db.getColumns("menu", menuColumn);

                String query = "SELECT D.menu_id, D.count\n" +
                        "                FROM (SELECT menu_id, COUNT(menu_id)\n" +
                        "                      FROM (\n" +
                        "                               SELECT menu_id\n" +
                        "                               FROM order_menu_link\n" +
                        "                               WHERE order_id IN (\n" +
                        "                                   SELECT order_id AS \"resp\"\n" +
                        "                                   FROM \"order\"\n" +
                        "                                     WHERE order_date BETWEEN " + "'" + fromDate + "'" + "and " + "'" + toDate + "'\n" +
                        "                               )\n" +
                        "\n" +
                        "                           ) AS T\n" +
                        "                      GROUP BY menu_id) D\n" +
                        "                         JOIN (SELECT t1.menu_id, t1.price\n" +
                        "                               FROM menu t1\n" +
                        "                               WHERE menu_id IN (\n" +
                        "                                   SELECT menu_id\n" +
                        "                                   FROM (\n" +
                        "                                            SELECT menu_id\n" +
                        "                                            FROM order_menu_link\n" +
                        "                                            WHERE order_id IN (\n" +
                        "                                                SELECT order_id AS \"resp\"\n" +
                        "                                                FROM \"order\"\n" +
                        "                                                     WHERE order_date BETWEEN " + "'" + fromDate + "'" + "and " + "'" + toDate + "'\n" +
                        "                                            )\n" +
                        "                                        ) AS T\n" +
                        "\n" +
                        "                                   GROUP BY menu_id\n" +
                        "                               )\n" +
                        "                )\n" +
                        "                V\n" +
                        "\n" +
                        "                            ON D.menu_id = V.menu_id\n" +
                        "                            ORDER BY d.count DESC";

                // gets menu_ids and count of each menu item into an arraylist
                ArrayList<HashMap<String,String>> orderData = db.customQuery(query);
                // loop through and display data
                // limits list to 10 menu items, clears list for next check
                popList.getItems().clear();
                int counter = 0;
                for (HashMap<String, String> order : orderData){
                        if (counter > 9) {
                                break;
                        }
                        for (HashMap<String, String> ID : menuData){
                                String currID = order.get("menu_id");
                                if (currID.equals(ID.get("menu_id"))){
                                        String foodName = ID.get("menu_name");
                                        popList.getItems().add("Menu ID: " + order.get("menu_id") + " | Name: " + foodName + " | Count: "  + order.get("count"));
                                }
                        }
                        counter++;
                }
        }

        public void initialize(){
                db = new dbConnections();
                submitButton.setOnMouseClicked(mouseEvent -> submitHandler(mouseEvent));
        }

}
