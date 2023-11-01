package main.employee;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import main.employee.dbConnections.dbConnections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MainController {
    @FXML
    private VBox catlist;

    @FXML
    private ListView<Integer> checkID;

    @FXML
    private ListView<String> checkName;

    @FXML
    private ListView<String> checkPrice;

    @FXML
    private Label totalLabel;

    //@FXML
    private GridPane foodGrid;

    @FXML
    private Button SB;

    @FXML
    private BorderPane mainBorder;

    private dbConnections db;

    HashMap<Integer, HashMap<String, String>> menuItems = new HashMap<Integer, HashMap<String, String>>();
    HashMap<String, ArrayList<Integer>> menuCat = new HashMap<>();
    ArrayList<VBox> vboxList = new ArrayList<>();

    public void buttonHandler(MouseEvent e){
        Button pressed = (Button) e.getSource();
        String id = pressed.getId();
        int index = id.indexOf("FB");
        if(index > -1){
            id = id.substring(index+2);
            addToList(Integer.parseInt(id));
        }
        index = id.indexOf("SB");
        System.out.println(index);
        if(index > -1){
            sendData();
        }
    }

    public void sendData(){
        // Request order_id
        ArrayList<HashMap<String, String>> dataSent = new ArrayList<>();
        HashMap<String, String> hashSent = new HashMap<String, String>();
        hashSent.put("total_amount", "0.00");
        ArrayList<String> returnCol= new ArrayList<String>(Arrays.asList("order_id"));
        dataSent.add(hashSent);

        ArrayList<HashMap<String, String>> rData = db.insertData("\"order\"", dataSent, returnCol);

        // Send Links
        dataSent.clear();
        System.out.println(rData.get(0).get("order_id"));
        for(int id : checkID.getItems()){
            HashMap<String, String> orderItem = new HashMap<String, String>();
            orderItem.put("order_id", rData.get(0).get("order_id"));
            orderItem.put("menu_id", String.valueOf(id));
            dataSent.add(orderItem);
        }
        returnCol.clear();
        db.insertData("order_menu_link", dataSent, returnCol);

        checkID.getItems().clear();
        checkName.getItems().clear();
        checkPrice.getItems().clear();
        totalLabel.setText("$0.00");
    }

    public void addToList(int id){
        checkID.getItems().add(id);
        checkName.getItems().add(menuItems.get(id).get("menu_name"));
        checkPrice.getItems().add("$"+menuItems.get(id).get("price"));
        totalLabel.setText("$"+ String.format("%.2f" ,(Double.parseDouble(totalLabel.getText().substring(1))
                +Double.parseDouble(menuItems.get(id).get("price")))) );
    }


    public void categoryChange(MouseEvent e){
        Button pressed = (Button) e.getSource();
        String id = pressed.getId();


        int index = id.lastIndexOf("cat");
        id = id.substring(index+3);
        foodGrid.getChildren().clear();
        int i = 0;

        if(id.equals("ALL")){
            for(VBox boxB : vboxList){
                if (i % 2 == 0) {
                    RowConstraints RC = new RowConstraints(100);
                    foodGrid.getRowConstraints().add(RC);
                }
                foodGrid.add(boxB, i % 2, Math.floorDiv(i, 2));
                i++;
            }
            return;
        }

        for(Integer numId : menuCat.get(id)){
            for(VBox boxB : vboxList){
                String bid = boxB.getChildren().get(0).getId();
                bid = bid.substring(bid.indexOf("FB")+2);

                if(Integer.parseInt(bid) == numId) {
                    if (i % 2 == 0) {
                        RowConstraints RC = new RowConstraints(100);
                        foodGrid.getRowConstraints().add(RC);
                    }
                    foodGrid.add(boxB, i % 2, Math.floorDiv(i, 2));
                    i++;
                    continue;
                }
            }
        }
    }


    public void initialize(){
        // Start Connection with Database
        db = new dbConnections();

        // GridPane
        ArrayList<String> columns = new ArrayList<>(Arrays.asList("menu_id", "menu_name", "price", "category"));
        ArrayList<HashMap<String, String>> data = db.getColumns("menu", columns);

        SB.setOnMouseClicked(mouseEvent -> buttonHandler(mouseEvent));
        int i = 0;

        foodGrid = new GridPane();
        ScrollPane sp = new ScrollPane();
        sp.setContent(foodGrid);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setFitToWidth(true);
        foodGrid.prefHeightProperty().bind(sp.widthProperty());
        foodGrid.setAlignment(Pos.CENTER);
        foodGrid.setHgap(100);
        sp.setPadding(new Insets(50,20,20,50));
        mainBorder.setCenter(sp);

        System.out.println(foodGrid.getRowCount() + " " + (int) Math.ceil((data.size() - 6)/2.0));
        for(HashMap<String,String> item : data){
            JFXButton d = new JFXButton("Add \n"+item.get("menu_name")+"\nPrice: " + item.get("price"));
            d.setId("FB"+item.get("menu_id"));
            d.setAlignment(Pos.CENTER);
            VBox box = new VBox();
            d.setPadding(new Insets(10,5,10,5));
            d.setMaxWidth(Double.MAX_VALUE);
            d.textAlignmentProperty().set(TextAlignment.CENTER);
            d.setMinWidth(box.getPrefWidth());
            d.setOnMouseClicked(mouseEvent -> buttonHandler(mouseEvent));
            box.getChildren().add(d);
            if(i % 2 ==0 ){
                RowConstraints RC = new RowConstraints(100);
                foodGrid.getRowConstraints().add(RC);
            }
            d.setContentDisplay(ContentDisplay.CENTER);
            vboxList.add(box);
            foodGrid.add(box, i%2, Math.floorDiv(i, 2));
            i++;
            HashMap<String, String> t = new HashMap<String, String>();
            t.put("price", item.get("price").substring(1) );
            t.put("category", item.get("category"));
            t.put("menu_name", item.get("menu_name"));
            menuItems.put(Integer.parseInt(item.get("menu_id")), t);
        }


        for(Map.Entry<Integer,HashMap<String, String> > item : menuItems.entrySet()){
            if(menuCat.containsKey(item.getValue().get("category"))){
                menuCat.get(item.getValue().get("category")).add(item.getKey());
                continue;
            }
            menuCat.put(item.getValue().get("category"), new ArrayList<>(Arrays.asList(item.getKey())));
        }
        JFXButton tempB = new JFXButton("ALL");
        tempB.setPadding(new Insets(10,10,10,10));
        tempB.setMaxWidth(Double.MAX_VALUE);
        tempB.setId("catALL");
        tempB.setOnMouseClicked(mouseEvent -> categoryChange(mouseEvent));
        catlist.getChildren().add(tempB);

        for(String item : menuCat.keySet()){
            tempB = new JFXButton(item);
            tempB.setPadding(new Insets(10,10,10,10));
            tempB.setMaxWidth(Double.MAX_VALUE);
            tempB.setId("cat"+item);
            tempB.setOnMouseClicked(mouseEvent -> categoryChange(mouseEvent));
            catlist.getChildren().add(tempB);
        }
        /*
        checkID.setCellFactory(lv -> {
            ListCell<Integer> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem removeItem = new MenuItem();
            removeItem.setText("Delete");
            removeItem.setOnAction(event -> {
                checkID.getItems().remove(cell.getIndex());
                checkPrice.getItems().remove(cell.getIndex());
                checkName.getItems().remove(cell.getIndex());
            });



            cell.emptyProperty().addListener((observableValue, aBoolean, t1) -> {
                if(t1) {
                    cell.setContextMenu(null);
                }else{
                    cell.setContextMenu(contextMenu);
                }
            });

            return cell;
        });
    */
    }


}