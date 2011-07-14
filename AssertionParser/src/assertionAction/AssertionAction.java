package assertionAction;

import java.sql.SQLException;

import assertionAction.ActionExecuter.ActionWorker;

public interface AssertionAction {
    String doAction(ActionWorker worker) throws SQLException;
    String doCheck(ActionWorker worker) throws SQLException;

    String describe();
}
