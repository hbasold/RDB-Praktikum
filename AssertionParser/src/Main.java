import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import triggerGeneration.TriggerGenerator;
import assertionInsertion.AssertionInserter;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        if(args.length < 1){
            System.err.println("Expecting assertion file as argument");
        }
        else{
            try {
                // Datenbankverbindung
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://localhost:5432/rdb_praktikum";
                Connection sql = DriverManager.getConnection(url, "henning", "henning");

                String error = AssertionInserter.insertAssertions(args[0], sql);

                if(error == null){
                    System.out.println("Assertions have been successfully checked and saved.");
                    TriggerGenerator.createAssertions(sql);
                }
                else{
                    System.err.println(error);
                }
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

}
