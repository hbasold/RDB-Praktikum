import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import triggerGeneration.TriggerGenerator;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Datenbankverbindung
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/rdb_praktikum";
            Connection sql = DriverManager.getConnection(url, "henning", "henning");

            TriggerGenerator.createAssertions(sql);
        }
        catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

}
