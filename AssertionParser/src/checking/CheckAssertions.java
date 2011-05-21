/**
 * 
 */
package checking;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import parsing.Assertion;

/**
 * @author henning
 *
 */
public class CheckAssertions {
    
    private final String syntaxErrorString = ".* Syntaxfehler bei »(.*)«\\s+ Position: (\\d+).*";
    private final String notExistsErrorPattern = ".* (\\w+) »?([\\w.]+)«? existiert nicht\\s+ Position: (\\d+).*";
    
    Connection sql;
    Pattern syntaxErrorParser;
    Pattern notExistsErrorParser;

    public CheckAssertions(Connection conn) throws SQLException {
        sql = conn;
        checkTestTables();
        syntaxErrorParser = Pattern.compile(syntaxErrorString);
        notExistsErrorParser = Pattern.compile(notExistsErrorPattern);
    }

    private void checkTestTables() throws SQLException {
        Statement create = sql.createStatement();
        
        try {
            ResultSet exists = create.executeQuery("SELECT count(relname) AS hasTable FROM pg_class WHERE lower(relname) = lower('TestSysRel')");
            
            if(exists.next() && exists.getInt("hasTable") == 0){
                create.executeUpdate("CREATE TABLE TestSysRel (Attribut INTEGER NOT NULL, PRIMARY KEY (Attribut))");
                create.executeUpdate("INSERT INTO TestSysRel VALUES(1)");
            }
        }
        finally{
            create.close();
        }
    }

    public String check(Vector<Assertion> assertions) throws SQLException {
        for(Assertion a : assertions){
            String error = checkAssertion(a);
            if(error != null){
                return error;
            }
        }
        return null;
    }

    private String checkAssertion(Assertion a) throws SQLException {
        String error = checkName(a.name);
        if(error == null){
            error = checkPredicate(a.predicate);
        }
        
        if(error != null){
            error = "Error in assertion " + a.name + " on line " + a.line + ": " + error + ".";
        }
        
        return error;
    }

    private String checkName(String name) throws SQLException {
        Statement create = sql.createStatement();
        try {
            create.executeUpdate("CREATE TABLE " + name + " (Attribut INTEGER)");
            create.executeUpdate("DROP TABLE " + name);
        }
        catch (SQLException e) {
            // Leider funktionieren die Fehlercodes etc. mit dem Postgres-Backend scheinbar nicht...
            if(e.getMessage().contains(syntaxErrorString)){
                return "invalid SQL identifier";
            }
            else{
                throw e;
            }
        }
        finally {
            create.close();
        }       
        
        return null;
    }
    
    private String checkPredicate(String predicate) throws SQLException {
        Statement select = sql.createStatement();
        
        try {
            select.executeQuery("SELECT * FROM TestSysRel WHERE " + predicate);
        }
        catch (SQLException e) {
            // Leider funktionieren die Fehlercodes etc. mit dem Postgres-Backend scheinbar nicht...
            String error = e.getMessage();
            Matcher m = syntaxErrorParser.matcher(error);
            if(m.matches()){
                return "syntactic error in predicate at position " + m.group(2) + " near \"" + m.group(1) + "\"";
            }            
            else {
                m = notExistsErrorParser.matcher(error);
                if(m.matches()){
                    return "error in predicate at position " + m.group(3) + ": " + m.group(1) + " \"" + m.group(2) + "\" does not exist";
                }
                else{
                    throw e;
                }
            }
        }
        finally {
            select.close();
        }
        
        return null;
    }

}
