/**
 * 
 */
package checking;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.Vector;

import parsing.Assertion;

/**
 * @author henning
 *
 */
public class CheckAssertions {
    
    Connection sql;

    public CheckAssertions(Connection conn) {
        sql = conn;
        checkTestTables();
    }

    private void checkTestTables() {
        // TODO Auto-generated method stub
        
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
            //error = checkPredicate(a.predicate);
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
            if(e.getMessage().contains("Syntaxfehler")){
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

}
