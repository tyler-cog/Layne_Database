package import_scripts;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.sql.*;



public class importInvoiceDB {
    private static final int START_ROW = 2;
    private static final int SKU = 2;
    private static final int DESCRIPTION = 1;
    private static final int DELIVERED = 4;
    private static final int PRICE = 8;
    private static final int TOTAL = 9;
    private static final int CATEGORY = 10;
    private static final int LONG_DESCRIPTION = 12;
    private static final int DATE_COL = 6;
    private static final int DATE_ROW = 0;

    public static int importInvoiceInfo(Connection conn, String filename) {
        // Create Statement from database
        Statement stmt= null;
        StringBuilder sqlQ;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Parse CSV file
        ArrayList<ArrayList<String>> matrix = Csv2Array(filename);
        String DateInv = matrix.get(DATE_ROW).get(DATE_COL);

        sqlQ = new StringBuilder("""
                INSERT INTO invoice_line (sku, quantity, price_total)
                VALUES
                """);

        for(int i = START_ROW; i < matrix.size(); i++){
            ArrayList<String> currentRow = matrix.get(i);

            if(currentRow.size() < 12){
                continue;
            }

            sqlQ.append("(").append(currentRow.get(SKU).replaceAll("\"", "")).
                    append(", ").append(currentRow.get(DELIVERED).replaceAll("\"", "")).
                    append(", ").append(currentRow.get(TOTAL).replaceAll("\"", "")).append("), \n");
        }

        sqlQ = new StringBuilder(sqlQ.substring(0, sqlQ.lastIndexOf(",")));
        sqlQ.append(" RETURNING inv_line_entry_no;");
        System.out.println(sqlQ.toString());


        // Execution
        ResultSet response = null;
        ArrayList<Integer> invLineNo = new ArrayList<Integer>();
        String invoiceId = "";

        // Get InvoiceLineNO
        try {
            response = stmt.executeQuery(sqlQ.toString());
            while (response.next()){
                invLineNo.add(response.getInt("inv_line_entry_no"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }

        sqlQ = new StringBuilder(" INSERT INTO invoices (invoice_id, date) " +
                "VALUES (DEFAULT, "+DateInv+") RETURNING invoice_id;");

        System.out.println(sqlQ.toString());

        try {
            response = stmt.executeQuery(sqlQ.toString());
            while (response.next()){
                invoiceId = response.getString("invoice_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }

        sqlQ = new StringBuilder(" INSERT INTO invoices_entry (invoices_id, invoice_line_no)\nVALUES\n");

        for(int lineNo : invLineNo){
            sqlQ.append("( ").append("'").append(invoiceId).append("'").append(", ").append(lineNo).append("), \n");
        }

        sqlQ = new StringBuilder(sqlQ.substring(0, sqlQ.lastIndexOf(","))+";");
        System.out.println(sqlQ.toString());

        return executeQueryStrBuilder(stmt, sqlQ, conn);
    }

    protected static int executeQueryStrBuilder(Statement stmt, StringBuilder sqlQ, Connection conn) {
        try {
            stmt.executeUpdate(sqlQ.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }

        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return 0;
    }

    public static int  importItemsFromInvoice( Connection conn, String filename){
        // Create Statement from database
        Statement stmt= null;
        StringBuilder sqlQ;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Parse the array and build an SQL command
        ArrayList<ArrayList<String>> matrix = Csv2Array(filename);

        sqlQ = new StringBuilder("""
                INSERT INTO inventory_items (sku, quantity, category, price, description, quantity_per_order, usage_category)
                VALUES
                """);
        String CAT_CURRENT = "NONE";
        for(int i = START_ROW; i < matrix.size(); i++){
            ArrayList<String> currentRow = matrix.get(i);

            if(currentRow.size()< 2){
                if(currentRow.size() != 0){
                    CAT_CURRENT = currentRow.get(0);
                }
                continue;
            }
            if(currentRow.size()< 12){
                continue;
            }

            sqlQ.append("(").append(currentRow.get(SKU).replaceAll("\"", "")).append(", ").append(0).
                    append(", ").append(currentRow.get(CATEGORY).replaceAll("\"", "")).append(", ").
                    append(currentRow.get(PRICE).replaceAll("\"", "")).append(", ").
                    append(currentRow.get(DESCRIPTION).replaceAll("\"", "")).append(", ").
                    append(currentRow.get(LONG_DESCRIPTION).replaceAll("\"", "")).append(", ").
                    append(CAT_CURRENT).append("), \n");
        }
        sqlQ = new StringBuilder(sqlQ.substring(0, sqlQ.lastIndexOf(",")) + ";");

        System.out.println(sqlQ);

        // Execution
        return executeQueryStrBuilder(stmt, sqlQ, conn);
    }


    protected static ArrayList<ArrayList<String>> Csv2Array(String filename){
        // Read File
        File fileToRead = new File(filename);
        Scanner fileReader = null;

        try {
            fileReader = new Scanner(fileToRead);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }


        // Port the file to array
        ArrayList<ArrayList<String>> matrix = new ArrayList<>();
        while(fileReader.hasNext()){
            String currentLine = fileReader.nextLine();
            String[] row = currentLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            for(int i = 0; i < row.length; i++){
                row[i] = "'"+row[i].replaceAll("'", "''")+"'";
            }

            matrix.add(new ArrayList<>(Arrays.asList(row)));
        }
        return matrix;
    }
}
