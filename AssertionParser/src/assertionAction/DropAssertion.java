package assertionAction;

import java.sql.SQLException;

import assertionAction.ActionExecuter.ActionWorker;

public class DropAssertion implements AssertionAction {

    private int line;
    private String name;

    public DropAssertion(int line, String name) {
        this.line = line;
        this.name = name;
    }

    @Override
    public String doAction(ActionWorker worker) throws SQLException {
        return worker.drop(name);
    }

    @Override
    public String doCheck(ActionWorker worker) throws SQLException {
        return worker.checkDrop(name);
    }

    @Override
    public String describe() {
        return "DROP ASSERTION " + name + " (line " + line + ")";
    }

    @Override
    public String toString() {
        return describe();
    }

}
