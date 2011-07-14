package assertionAction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import parsing.Assertion;
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

        public String insert(Assertion a) throws SQLException{
            // Assertions speichern
            return insert.insertAssertion(a);
        }

        public String create(Assertion a) throws SQLException {
            return TriggerGenerator.createAssertion(sql, a);
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
                return "Error while executing " + a.describe() + " :" + error;
            }
        }
        return null;
    }

}
