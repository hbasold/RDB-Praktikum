package assertionAction;

import java.sql.SQLException;

import assertionAction.ActionExecuter.ActionWorker;

import parsing.Assertion;

public class InsertAssertion implements AssertionAction {

    Assertion a;

    public InsertAssertion(Assertion a) {
        this.a = a;
    }

    @Override
    public String doAction(ActionWorker worker) throws SQLException {
        return worker.insert(a);
    }

    @Override
    public String doCheck(ActionWorker worker) throws SQLException {
        return worker.checkInsert(a);
    }

    @Override
    public String describe() {
        return "INSERT ASSERTION " + a.name;
    }
    
    @Override
    public String toString() {
        return describe();
    }

}
