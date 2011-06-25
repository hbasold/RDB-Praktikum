import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parsing.Assertion;



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
            
            Vector<Assertion> assertions = getAssertions(sql);
            
            for(Assertion a : assertions){
                if(!a.implemented){
                    // Betroffene Relationen holen
                    Set<String> affectedTables = getAffectedTables(sql, a);
                    
                    System.out.println(a.name + ": ");
                    for(String t : affectedTables){
                        System.out.println("\t" + t);
                    }
                    
                    sql.setAutoCommit(false);
                    
                    try {
                        // Funktion für Assertion einfügen
                        createFunction(a, sql);
                        
                        // Trigger für die Relationen erzeugen
                        createTriggers(a, affectedTables, sql);
                        
                        // Status auf "implementiert" setzen
                        Statement setImplemented = sql.createStatement();
                        if(setImplemented.executeUpdate(
                                "UPDATE AssertionSysRel SET implementiert = true " +
                                "WHERE Assertionname = '" + a.name + "'")
                                == 1){                        
                            sql.commit();
                        }
                        else{
                            System.err.println("Could not update assertion " + a.name);
                        }
                    }
                    catch (SQLException e) {
                        System.err.println("Database error: " + e.getMessage());
                    }
                    finally {
                        sql.setAutoCommit(true);
                    }
                }
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

    /**
     * @param a
     * @param affectedTables
     * @param sql 
     * @throws SQLException 
     */
    private static void createTriggers(Assertion a, Set<String> affectedTables, Connection sql) throws SQLException {
        Statement create = sql.createStatement();
        
        for(String t : affectedTables){
            StringBuilder triggerString = new StringBuilder();
            Formatter triggerFormatter = new Formatter(triggerString);
            triggerFormatter.format(
                    "CREATE TRIGGER CHECK_%1$s_%2$s" +
                    "  AFTER INSERT or UPDATE or DELETE ON %2$s" +
                    "  FOR EACH ROW EXECUTE PROCEDURE CHECK_%1$s();",
                    a.name, t);
            
            create.executeUpdate(triggerString.toString());
        }
    }

    /**
     * @param a
     * @param sql 
     * @throws SQLException 
     */
    private static void createFunction(Assertion a, Connection sql) throws SQLException {
        
        StringBuilder functionString = new StringBuilder();
        Formatter functionFormatter = new Formatter(functionString);
        functionFormatter.format(
                "CREATE FUNCTION %1$s() RETURNS TRIGGER AS\n" +
        		"'Declare res RECORD;\n" +
        		"BEGIN\n" +
        		"  SELECT INTO res COUNT(*) AS num\n" +
        		"  FROM TestSysRel\n" +
        		"  WHERE NOT (\n" +
        		"    %2$s\n" +
        		"  )\n;" +
        		"  IF (res.num > 0)\n" +
        		"  THEN RAISE EXCEPTION\n" +
        		"    ''ASSERTION %1$s violated!'';\n" +
        		"  END IF;\n" +
        		"  RETURN NEW;\n" +
        		"END;'\n" +
        		"LANGUAGE 'plpgsql';",
        		"CHECK_" + a.name, a.predicate);
        
        Statement create = sql.createStatement();
        create.executeUpdate(functionString.toString());
    }

    private static Set<String> getAffectedTables(Connection sql, Assertion a) throws SQLException {
        Pattern tableExtraction = Pattern.compile(".* Scan.* on (\\w+) .*");
        
        Set<String> affectedTables = new CopyOnWriteArraySet<String>();
        Statement check = sql.createStatement();
        ResultSet pred = check.executeQuery("EXPLAIN SELECT * FROM TestSysRel WHERE " + a.predicate);                
        while(pred.next()){
            Matcher match = tableExtraction.matcher(pred.getString("QUERY PLAN"));
            if(match.find()){
                String table = match.group(1);
                if(!table.equalsIgnoreCase("TestSysRel")){
                    affectedTables.add(table);
                }
            }
        }
        
        return affectedTables;
    }

    private static Vector<Assertion> getAssertions(Connection sql) throws SQLException {
        Vector<Assertion> as = new Vector<Assertion>();
        
        Statement assertionsQuery = sql.createStatement();
        ResultSet assertions = assertionsQuery.executeQuery("SELECT * FROM AssertionSysRel");
        while(assertions.next()){
            Assertion a = new Assertion(0, assertions.getString("Assertionname"), assertions.getString("Bedingung"), assertions.getBoolean("implementiert"));
            as.add(a);
        }
        return as;
    }

}
