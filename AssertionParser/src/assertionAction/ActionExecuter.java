package assertionAction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import parsing.Assertion;
import parsing.Either;
import triggerGeneration.TriggerGenerator;

import checking.CheckAssertions;
import checking.InsertAssertions;

public class ActionExecuter {

    public static class ActionWorker {
        private Connection sql;
        private CheckAssertions check;
        private InsertAssertions insert;

        public ActionWorker(Connection sql) throws SQLException {
            this.sql = sql;
            check = new CheckAssertions(sql);
            insert = new InsertAssertions(sql);
        }

        public String checkInsert(Assertion a) throws SQLException{
            return check.checkAssertion(a);
        }

        public Either<String, Boolean> insert(Assertion a) throws SQLException{
            // Assertions speichern
            return insert.insertAssertion(a);
        }

        public String create(Assertion a) throws SQLException {
            return TriggerGenerator.createAssertion(sql, a);
        }

        public String checkDrop(String assertionName) throws SQLException{
            Statement assertionsQuery = sql.createStatement();
            ResultSet implemented = assertionsQuery.executeQuery("SELECT implementiert FROM AssertionSysRel WHERE Assertionname='" + assertionName + "'");
            if(!implemented.next()){
                return "Assertion not found.";
            }
            else{
                if(!implemented.getBoolean("implementiert")){
                    return "Assertion not implemented.";
                }
                else{
                    return null;
                }
            }
        }

        public String drop(String assertionName) throws SQLException{
            String error = TriggerGenerator.drop(sql, assertionName);
            if(error == null){
                // drop from AssertionSysRel
                Statement drop = sql.createStatement();
                drop.executeUpdate("DELETE FROM AssertionSysRel WHERE Assertionname='" + assertionName + "'");
            }
            return error;
        }

        public String checkCheck(String assertionName) throws SQLException{
            Statement assertionsQuery = sql.createStatement();
            ResultSet implemented = assertionsQuery.executeQuery("SELECT implementiert FROM AssertionSysRel WHERE Assertionname='" + assertionName + "'");
            if(!implemented.next()){
                return "Assertion not found.";
            }
            else{
                if(!implemented.getBoolean("implementiert")){
                    return "Assertion not implemented.";
                }
                else{
                    return null;
                }
            }
        }

        public String checkAssertion(String assertionName) throws SQLException{
            Statement validationQuery = sql.createStatement();
            ResultSet valid = validationQuery.executeQuery("SELECT " + "check_" + assertionName + "_standalone() as res");
            if(!valid.next()){
                return "Could not check assertion.";
            }
            else{
                if(!valid.getBoolean("res")){
                    return "Assertion violated.";
                }
                else{
                    return null;
                }
            }
        }
    }

    private ActionWorker worker;

    public ActionExecuter(Connection sql) throws SQLException {
        worker = new ActionWorker(sql);
    }

    public String check(Vector<AssertionAction> assertions) throws SQLException {
        for(AssertionAction a : assertions){
            String error = a.doCheck(this.worker);
            if(error != null){
                return "Error while checking " + a.describe() + " :\n" + error;
            }
        }
        return null;
    }

    public String exec(Vector<AssertionAction> assertions) throws SQLException {
        for(AssertionAction a : assertions){
            String error = a.doAction(this.worker);
            if(error != null){
                return "Error while executing " + a.describe() + " :\n" + error;
            }
        }
        return null;
    }

}
