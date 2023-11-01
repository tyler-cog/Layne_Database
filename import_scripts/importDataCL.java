package import_scripts;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;
import java.sql.*;

public class importDataCL {
    public static void main(String[] args){
        // Show Files available
        File directory = new File(".");
        System.out.println("Select Invoice File to Import: ");

        int counter = 0;
        for(File file : Objects.requireNonNull(directory.listFiles())){
            System.out.println("["+counter+"] "+ file.getName());
            counter++;
        }

        // File Selection
        Scanner inputRead = new Scanner(System.in);

        System.out.print("\nSelection Number: ");
        String fileName = Objects.requireNonNull(directory.listFiles())[inputRead.nextInt()].getName();

        // Database Information
        inputRead.nextLine();
        System.out.print("Connection URL: ");
        String dbUrl = inputRead.nextLine();
        System.out.print("UserName: ");
        String dbUser = inputRead.nextLine();
        System.out.print("Password: ");
        String dbPass = inputRead.nextLine();

        // Attempt Connection to Database
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        }catch (Exception e){
            System.out.println("Connection Failed :(");
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("\n Connection Succeeded!");
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Use that Connection to Parse the file
        System.out.println("Want to import to invoice (0) or inventory items (1) or Order (2): ");
        int userResponse = inputRead.nextInt();
        inputRead.close();

        if(userResponse == 0){
            if(importInvoiceDB.importInvoiceInfo(conn, fileName) >= 0){
                System.out.println("Finished Successfully!");
            }else{
                System.out.println("An issue has occurred!");
            }
        }else if(userResponse == 1){
            if(importInvoiceDB.importItemsFromInvoice(conn, fileName) >= 0){
                System.out.println("Finished Successfully!");
            }else{
                System.out.println("An issue has occurred!");
            }
        }else{
            if(importOrderDB.importOrderFromCSV(conn, fileName) >= 0){
                System.out.println("Finished Successfully!");
            }else{
                System.out.println("An issue has occurred!");
            }
        }

        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
