package be.uantwerpen.sd.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/mealplanner";
        String user = "student";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {

            System.out.println("Connected to PostgreSQL database!");

            // Drop table if exists
            stmt.execute("DROP TABLE IF EXISTS test_table");
            System.out.println("Dropped test_table (if it existed).");

            // Create table
            stmt.execute("CREATE TABLE test_table (id SERIAL PRIMARY KEY, name VARCHAR(255))");
            System.out.println("Created test_table successfully.");

            // Drop table if exists
            stmt.execute("DROP TABLE IF EXISTS test_table");
            System.out.println("Dropped test_table (if it existed).");

        } catch (Exception e) {
            System.err.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
