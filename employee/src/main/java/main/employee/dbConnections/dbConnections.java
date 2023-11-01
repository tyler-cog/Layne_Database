package main.employee.dbConnections;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class dbConnections {
    private Connection conn;

    /**
     * Initiates the Connection with the Database
     * @param url String url of the db
     * @param user String username of the db
     * @param pass String pass of the db
     * @return Connection object of db
     */
    public static Connection startConnection(String url, String user, String pass){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Connection Failed!");
            System.exit(1);
        }
        return conn;
    }

    /**
     * Creates connection using team credentials
     * @return Connection object of db
     */
    public static Connection myConnection(){
        return startConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu:5432/csce315907_40db",
                "csce315907_40user", "group40ftw");
    }

    /**
     * Creates the dbConnection Object
     * @param url String url of the db
     * @param user String username of the db
     * @param pass String password of the db
     */
    public dbConnections(String url, String user, String pass){
        conn = startConnection(url, user, pass);
    }

    /**
     * Creates the dbConnection Object with Teams credentials
     */
    public dbConnections(){
        conn = myConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public ArrayList<HashMap<String,String>> customQuery(String query){
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // Return Data
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        try {
            ResultSet resp = stmt.executeQuery(query);
            while (resp.next()){
                data.add(new HashMap<String, String>());
                for(int i = 0; i < resp.getMetaData().getColumnCount(); i++){
                    data.get(data.size()-1).put(resp.getMetaData().getColumnName(i+1), resp.getString(resp.getMetaData().getColumnName(i+1)));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close Statement
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return data;
    }

    /**
     * Getter for the Connection object;
     * @return Connection object
     */
    public Connection getConn(){
        return conn;
    }

    /**
     * Gets all the data from the colArray from the tableName, each row is an entry, and
     * each column is data requested from the colArray
     * @param tableName name of the tablename
     * @param colArray HashMap of column names
     * @return data matrix
     */
    public ArrayList<HashMap<String,String>> getColumns(String tableName, ArrayList<String> colArray){
        // Create the query
        StringBuilder queryDB = new StringBuilder("SELECT ");

        // Columns
        for(int i = 0; i< colArray.size()-1; i++){
            queryDB.append(colArray.get(i)).append(", ");
        }
        queryDB.append(colArray.get(colArray.size()-1)).append("\n");

        // Table
        queryDB.append("FROM ").append(tableName).append(";");

        // Execute the command
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // Return Data
        ArrayList<HashMap<String, String>> data = new ArrayList<>();

        try {
            ResultSet resp = stmt.executeQuery(queryDB.toString());
            while (resp.next()){
                data.add(new HashMap<String, String>());
                for(String col : colArray){
                    data.get(data.size()-1).put(col, resp.getString(col));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close Statement
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return data;
    }

    public void updateIndividual(String tableName, HashMap<String, String> values, String whereCol,
                                                               String isEqualTo){
        // Query Creator
        StringBuilder query = new StringBuilder("UPDATE ").append(tableName).append(" SET\n");
        ArrayList<String> colVal = new ArrayList<String>(values.keySet());
        for(int i = 0;i < colVal.size() -1 ;i++){
            query.append(colVal.get(i)).append(" = ").append(values.get(colVal.get(i))).append(", \n");
        }
        query.append(colVal.get(colVal.size() -1)).append(" = ").append("'").append(values.get(colVal.get(colVal.size()-1))).append("'");
        query.append("WHERE ").append(whereCol).append(" = ").append("'").append(isEqualTo).append("'").append(";");
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            stmt.executeUpdate(query.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(1);
        }
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public ArrayList<HashMap<String, String>> insertData(String tableName, ArrayList<HashMap<String,String>> insertData,
                                                         ArrayList<String> returnColumns){
        if(insertData.size() == 0){
            return null;
        }

        // Query
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableName).append(" (");
        ArrayList<String> insertCol = new ArrayList<String>(insertData.get(0).keySet());

        for(int i = 0; i< insertCol.size() -1; i++){
            query.append(insertCol.get(i) + ", ");

        }
        query.append(insertCol.get(insertCol.size()-1)).append(") ").append("\nVALUES\n");

        // Parse insertData

        for(int i =0; i<insertData.size() -1 ;i++){
            query.append("( ");
            for(int j = 0; j< insertCol.size() -1; j++){
                query.append("'").append(insertData.get(i).get(insertCol.get(j))).append("'").append(", ");
            }
            query.append("'").append(insertData.get(i).get(insertCol.get(insertCol.size() -1))).append("'").append("), \n");
        }

        query.append("( ");
        for(int j = 0; j< insertCol.size() -1; j++){
            query.append("'").append(insertData.get(insertData.size() -1).get(insertCol.get(j))).append("'").append(", ");
        }
        query.append("'").append(insertData.get(insertData.size() -1).get(insertCol.get(insertCol.size() -1))).append("'").append(") \n");

        if(returnColumns.size() > 0){
            query.append("RETURNING ");
            for(int i = 0; i< returnColumns.size() -1;i ++){
                query.append(returnColumns.get(i)).append(", ");
            }
            query.append(returnColumns.get(returnColumns.size()-1));
        }
        query.append(";");

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // Return Data
        ArrayList<HashMap<String, String>> data = null;
        if(returnColumns.size() > 0){
            data = new ArrayList<>();
        }

        try {
            if(returnColumns.size() > 0){
                ResultSet resp = stmt.executeQuery(query.toString());
                while (resp.next()){
                    data.add(new HashMap<String, String>());
                    for(String col : returnColumns){
                        data.get(data.size()-1).put(col, resp.getString(col));
                    }
                }
            }else{
                stmt.executeUpdate(query.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close Statement
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return data;

    }

    /**
     * Close the db Connection
     */
    public void closeConnection(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
