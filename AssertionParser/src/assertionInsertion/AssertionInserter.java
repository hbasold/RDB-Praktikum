package assertionInsertion;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import parsing.Assertion;
import parsing.AssertionParseError;
import parsing.AssertionParser;
import checking.CheckAssertions;
import checking.InsertAssertions;

public class AssertionInserter {

    /**
     * @param fileName
     * @param conn
     * @return
     * @throws FileNotFoundException
     * @throws AssertionParseError
     * @throws SQLException
     */
    public static String insertAssertions(String fileName, Connection conn) throws SQLException {
        try {
            // Assertions parsen
            FileReader input = new FileReader(fileName);
            AssertionParser parser = new AssertionParser();
            Vector<Assertion> assertions = parser.parse(input);
    
            // Assertions prüfen
            CheckAssertions check = new CheckAssertions(conn);
            String error = check.check(assertions);
            if(error == null){
                // Assertions speichern
                InsertAssertions insert = new InsertAssertions(conn);
                error = insert.insert(assertions);
            }
            return error;
        }
        catch (FileNotFoundException e) {
            return "Could not open file \"" + fileName + "\": " + e.getMessage();
        }
        catch (AssertionParseError e) {
            return "Parse error: " + e.getMessage();
        }
    }

}
