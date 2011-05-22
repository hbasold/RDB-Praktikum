package checking;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import parsing.Assertion;

public class InsertAssertions {
    
    private final String doubleEntryErrorString = "FEHLER: doppelter Schlüsselwert verletzt Unique-Constraint »assertionsysrel_pkey«";

    private Connection sql;

    public InsertAssertions(Connection conn) throws SQLException {
        sql = conn;
        checkTables();
    }
    
    private void checkTables() throws SQLException {
        Statement create = sql.createStatement();
        
        try {
            ResultSet exists = create.executeQuery("SELECT count(relname) AS hasTable FROM pg_class WHERE lower(relname) = lower('AssertionSysRel')");
            
            if(exists.next() && exists.getInt("hasTable") == 0){
                create.executeUpdate(
                    "CREATE TABLE AssertionSysRel (" +
                        "Assertionname VARCHAR(40) NOT NULL," +
                        "Bedingung VARCHAR(800) NOT NULL," +
                        "implementiert BOOL DEFAULT FALSE," +
                        "PRIMARY KEY (Assertionname)" +
                    ")"
                );
            }
        }
        finally{
            create.close();
        }
    }

    public String insert(Vector<Assertion> assertions) throws SQLException {
        for(Assertion a : assertions){
            String error = insertAssertion(a);
            if(error != null){
                return error;
            }
        }
        return null;
    }

    private String insertAssertion(Assertion a) throws SQLException {
        String error = insertAssertion_(a);
        if(error != null){
            error = "Error in assertion " + a.name + " on line " + a.line + ": \n\t" + error + ".";
        }
        return error;
    }

    private String insertAssertion_(Assertion a) throws SQLException {
        Statement create = sql.createStatement();
        
        try {
            if(isReinsertion(a)){
                System.out.println("Warning: assertion " + a.name + " already exists with same predicate. It is not inserted again");
            }
            else{
                create.executeUpdate("INSERT INTO AssertionSysRel VALUES('" + a.name + "','" + a.predicate + "',false)");
            }
        }
        catch (SQLException e) {
            // Leider funktionieren die Fehlercodes etc. mit dem Postgres-Backend scheinbar nicht...
            String error = e.getMessage();
            if(error.equals(doubleEntryErrorString)){
                if(!isReinsertion(a)){
                    return "assertion already exists with another predicate";
                }
            }
            
            throw e;
        }
        
        return null;
    }

    private boolean isReinsertion(Assertion a) throws SQLException {
        Statement check = sql.createStatement();
        
        ResultSet pred = check.executeQuery("SELECT Bedingung FROM AssertionSysRel WHERE Assertionname='" + a.name + "'");
        if(pred.next()){
            String p = pred.getString(1);
            return p.equals(a.predicate);
        }
        else{
            return false;
        }
    }
}
