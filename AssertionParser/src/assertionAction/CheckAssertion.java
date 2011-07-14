package assertionAction;

import java.sql.SQLException;

import assertionAction.ActionExecuter.ActionWorker;

public class CheckAssertion implements AssertionAction {

    private int line;
    private String name;

    public CheckAssertion(int line, String name) {
        this.line = line;
        this.name = name;
    }

    @Override
    public String doAction(ActionWorker worker) throws SQLException {
        return worker.checkAssertion(name);
    }

    @Override
    public String doCheck(ActionWorker worker) throws SQLException {
        return worker.checkCheck(name);
    }

    @Override
    public String describe() {
        return "CHECK ASSERTION " + name + " (line " + line + ")";
    }

    @Override
    public String toString(){
        return describe();
    }
}
