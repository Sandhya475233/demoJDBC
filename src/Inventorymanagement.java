import java.sql.*;
import java.util.Scanner;

public class Inventorymanagement {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {
            // Database Connection
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/retail_db",
                    "root",
                    "Sandhya@123"
            );

            System.out.println("Database Connected Successfully!");

            int choice;

            do {
                System.out.println("\n===== INVENTORY MANAGEMENT SYSTEM =====");
                System.out.println("1. Insert Product Records");
                System.out.println("2. Retrieve Products by Category");
                System.out.println("3. Update Product Stock");
                System.out.println("4. Search and Modify Product Price");
                System.out.println("5. Total Inventory Value and Delete");
                System.out.println("0. Exit");

                System.out.print("Enter your choice: ");
                choice = sc.nextInt();

                switch (choice) {

                    // (a) INSERT PRODUCT RECORDS
                    case 1:

                        String q1 = "INSERT INTO Product VALUES (?, ?, ?, ?, ?)";

                        PreparedStatement ps1 = con.prepareStatement(q1);

                        int[] ids = {201, 202, 203};
                        String[] names = {"Laptop", "Mouse", "Keyboard"};
                        String[] categories = {
                                "Electronics", "Electronics", "Electronics"
                        };
                        int[] quantities = {15, 10, 25};
                        double[] prices = {45000, 500, 1200};

                        for (int i = 0; i < 3; i++) {

                            try {
                                ps1.setInt(1, ids[i]);
                                ps1.setString(2, names[i]);
                                ps1.setString(3, categories[i]);
                                ps1.setInt(4, quantities[i]);
                                ps1.setDouble(5, prices[i]);

                                ps1.executeUpdate();

                                System.out.println(names[i] + " Inserted Successfully");

                            } catch (SQLIntegrityConstraintViolationException e) {
                                System.out.println("Duplicate Product ID: " + ids[i]);
                            }
                        }

                        ps1.close();
                        break;


                    // (b) CONDITIONAL RETRIEVAL AND SORTING
                    case 2:

                        sc.nextLine();

                        System.out.print("Enter Category: ");
                        String category = sc.nextLine();

                        String q2 = "SELECT * FROM Product " +
                                "WHERE category=? AND quantity<20 " +
                                "ORDER BY price DESC";

                        PreparedStatement ps2 = con.prepareStatement(q2);

                        ps2.setString(1, category);

                        ResultSet rs2 = ps2.executeQuery();

                        while (rs2.next()) {

                            System.out.println(
                                    rs2.getInt("product_id") + " " +
                                            rs2.getString("product_name") + " " +
                                            rs2.getString("category") + " " +
                                            rs2.getInt("quantity") + " " +
                                            rs2.getDouble("price")
                            );
                        }

                        rs2.close();
                        ps2.close();
                        break;


                    // (c) UPDATE PRODUCT STOCK
                    case 3:

                        System.out.print("Enter Product ID: ");
                        int id = sc.nextInt();

                        System.out.print("Enter Quantity to Add: ");
                        int qty = sc.nextInt();

                        String q3 = "UPDATE Product " +
                                "SET quantity = quantity + ? " +
                                "WHERE product_id = ?";

                        PreparedStatement ps3 = con.prepareStatement(q3);

                        ps3.setInt(1, qty);
                        ps3.setInt(2, id);

                        int rows = ps3.executeUpdate();

                        if (rows > 0) {
                            System.out.println("Stock Updated Successfully");
                        } else {
                            System.out.println("Product Does Not Exist");
                        }

                        ps3.close();
                        break;


                    // (d) SEARCH AND MODIFY PRODUCT DETAILS
                    case 4:

                        System.out.print("Enter Product ID: ");
                        int pid = sc.nextInt();

                        System.out.print("Enter New Price: ");
                        double newPrice = sc.nextDouble();

                        String q4 = "SELECT * FROM Product WHERE product_id=?";

                        PreparedStatement ps4 = con.prepareStatement(q4);

                        ps4.setInt(1, pid);

                        ResultSet rs4 = ps4.executeQuery();

                        if (rs4.next()) {

                            if (newPrice > 0) {

                                String q5 = "UPDATE Product SET price=? WHERE product_id=?";

                                PreparedStatement ps5 = con.prepareStatement(q5);

                                ps5.setDouble(1, newPrice);
                                ps5.setInt(2, pid);

                                ps5.executeUpdate();

                                System.out.println("Price Updated Successfully");

                                ps5.close();

                                // Display Updated Details
                                String q6 = "SELECT * FROM Product WHERE product_id=?";

                                PreparedStatement ps6 = con.prepareStatement(q6);

                                ps6.setInt(1, pid);

                                ResultSet rs6 = ps6.executeQuery();

                                if (rs6.next()) {
                                    System.out.println("Product ID: " + rs6.getInt("product_id"));
                                    System.out.println("Product Name: " + rs6.getString("product_name"));
                                    System.out.println("Category: " + rs6.getString("category"));
                                    System.out.println("Quantity: " + rs6.getInt("quantity"));
                                    System.out.println("New Price: " + rs6.getDouble("price"));
                                }

                                rs6.close();
                                ps6.close();

                            } else {
                                System.out.println("Price Must Be Greater Than Zero");
                            }

                        } else {
                            System.out.println("Product Not Found");
                        }

                        rs4.close();
                        ps4.close();
                        break;


                    // (e) AGGREGATE QUERY AND CONDITIONAL DELETION
                    case 5:

                        String q7 = "SELECT SUM(quantity * price) FROM Product";

                        PreparedStatement ps7 = con.prepareStatement(q7);

                        ResultSet rs7 = ps7.executeQuery();

                        if (rs7.next()) {
                            System.out.println("Total Inventory Value: " + rs7.getDouble(1));
                        }

                        rs7.close();
                        ps7.close();

                        System.out.print("Enter Product ID to Delete: ");
                        int did = sc.nextInt();

                        String q8 = "DELETE FROM Product " +
                                "WHERE product_id=? AND quantity=0";

                        PreparedStatement ps8 = con.prepareStatement(q8);

                        ps8.setInt(1, did);

                        int deleted = ps8.executeUpdate();

                        if (deleted > 0) {
                            System.out.println("Product Deleted Successfully");
                        } else {
                            System.out.println("Product Cannot Be Deleted");
                        }

                        ps8.close();
                        break;


                    case 0:
                        System.out.println("Thank You!");
                        break;


                    default:
                        System.out.println("Invalid Choice!");

                }

            } while (choice != 0);

            con.close();
            sc.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}