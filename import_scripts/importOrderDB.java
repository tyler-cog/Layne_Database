package import_scripts;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class importOrderDB {
    private static final int START = 1;
    private static final int DATE_COL = 0;
    private static final int ITEM_NUMBER = 1;
    private static final int QUANTITY = 2;

    public static int importOrderFromCSV(Connection conn, String fileName){
        // Create Data Matrix
        ArrayList<ArrayList<String>> matrix = importInvoiceDB.Csv2Array(fileName);

        // Create a statement
        Statement stmt = null;
        try {
             stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Start PostGreSQL Query
        ArrayList<String> orderIDs = new ArrayList<String>();

        StringBuilder sqlQ = new StringBuilder("""
                INSERT INTO "order" (order_date, total_amount) VALUES\n""");

        String date= "";
        ArrayList<String> dateList = new ArrayList<String>();

        for(int i = START; i < matrix.size(); i++){
            ArrayList<String> currentRow = matrix.get(i);
            if(currentRow.size() < 2){
                continue;
            }

            if(currentRow.size() == 5){
                date = currentRow.get(DATE_COL);
                dateList.add(date);
                continue;
            }
            sqlQ.append("(").append(date).append(", '$0.00'),\n");
        }

        sqlQ = new StringBuilder(sqlQ.substring(0, sqlQ.lastIndexOf(",")));
        sqlQ.append(" RETURNING order_id;");

        System.out.println(sqlQ.toString());
        ResultSet response = null;

        try {
            response = stmt.executeQuery(sqlQ.toString());
            while(response.next()){
                orderIDs.add(response.getString("order_id"));
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }

        sqlQ = new StringBuilder("""
                INSERT INTO order_menu_link (order_id, menu_id)
                VALUES
                """);

        int orderIDCounter = 0;
        for(int i = START; i < matrix.size(); i++){
            ArrayList<String> currentRow = matrix.get(i);
            if(currentRow.size() < 2){
                continue;
            }
            if(currentRow.size() == 5){
                continue;
            }
            for(int j =0; j< Integer.parseInt(currentRow.get(QUANTITY).replaceAll("'", "")); j++){
                sqlQ.append("('").append(orderIDs.get(orderIDCounter)).append("', ")
                        .append(currentRow.get(ITEM_NUMBER)).append("), \n");
            }
            orderIDCounter++;
        }
        System.out.println(sqlQ.toString());
        sqlQ = new StringBuilder(sqlQ.substring(0,sqlQ.lastIndexOf(","))+";");
        return importInvoiceDB.executeQueryStrBuilder(stmt, sqlQ, conn);
    }
}
