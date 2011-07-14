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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String doCheck(ActionWorker worker) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String describe() {
        return "CHECK ASSERTION " + name;
    }

    @Override
    public String toString(){
        return describe();
    }
}
