import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import checking.CheckAssertions;
import checking.InsertAssertions;

import parsing.Assertion;
import parsing.AssertionParseError;
import parsing.AssertionParser;


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
                // Assertions parsen
                FileReader input = new FileReader(args[0]);
                AssertionParser parser = new AssertionParser();
                Vector<Assertion> assertions = parser.parse(input);
                
                // Datenbankverbindung
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://localhost:5432/rdb_praktikum";
                Connection conn = DriverManager.getConnection(url, "henning", "henning");

                // Assertions prüfen
                CheckAssertions check = new CheckAssertions(conn);
                String error = check.check(assertions);
                if(error != null){
                    System.err.println(error);
                }
                else{
                    // Assertions speichern
                    InsertAssertions insert = new InsertAssertions(conn);
                    error = insert.insert(assertions);
                    if(error != null){
                        System.err.println(error);
                    }
                }
                
                if(error == null){
                    System.out.println("Assertions have been successfully checked and saved.");
                }
            }
            catch (FileNotFoundException e) {
                System.err.println("Could not open file \"" + args[0] + "\": " + e.getMessage());
            }
            catch (AssertionParseError e) {
                System.err.println("Parse error:" + e.getMessage());
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
