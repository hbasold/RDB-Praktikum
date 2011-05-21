import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;

import checking.CheckAssertions;

import parsing.Assertion;
import parsing.AssertionParseError;
import parsing.AssertionParser;


public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        if(args.length < 1){
            System.err.println("Expecting assertion file as argument");
        }
        else{
            try {
                FileReader input = new FileReader(args[0]);
                AssertionParser parser = new AssertionParser();
                Vector<Assertion> assertions = parser.parse(input);
                System.out.println(assertions);
                
                CheckAssertions check = new CheckAssertions();
                check.check(assertions);
            }
            catch (FileNotFoundException e) {
                System.err.println("Could not open file \"" + args[0] + "\": " + e.getMessage());
            }
            catch (AssertionParseError e) {
                System.err.println("Parse error:" + e.getMessage());
            }
        }
    }

}
