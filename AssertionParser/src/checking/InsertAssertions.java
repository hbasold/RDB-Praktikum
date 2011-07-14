package checking;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import parsing.Assertion;
import parsing.Either;

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

    public Either<String, Boolean> insertAssertion(Assertion a) throws SQLException {
        Either<String, Boolean> error = insertAssertion_(a);
        if(error.isLeft()){
            error = Either.Left("Error in assertion " + a.name + " on line " + a.line + ": \n    " + error.left() + ".").get();
        }
        return error;
    }

    private Either<String, Boolean> insertAssertion_(Assertion a) throws SQLException {
        Statement create = sql.createStatement();

        try {
            if(isReinsertion(a)){
                System.out.println("Warning: assertion " + a.name + " on line " + a.line + " already exists with same predicate.\n" +
                                    "    It is not inserted again.");
                return Either.Right(false).get();
            }
            else{
                create.executeUpdate("INSERT INTO AssertionSysRel VALUES('" + a.name + "','" + a.predicate + "',false)");
                return Either.Right(true).get();
            }
        }
        catch (SQLException e) {
            // Leider funktionieren die Fehlercodes etc. mit dem Postgres-Backend scheinbar nicht...
            String error = e.getMessage();
            if(error.equals(doubleEntryErrorString)){
                if(!isReinsertion(a)){
                    return Either.Left("assertion already exists with another predicate").get();
                }
            }

            throw e;
        }
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
