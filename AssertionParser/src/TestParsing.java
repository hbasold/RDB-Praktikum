import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;


public class TestParsing {

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
                Parser parser = new Parser();
                Vector<Assertion> assertions = parser.parse(input);
                System.out.println(assertions);
            }
            catch (FileNotFoundException e) {
                System.err.println("Could not open file \"" + args[0] + "\": " + e.getMessage());
            }
            catch (ParseError e) {
                System.err.println("Parse error:" + e.getMessage());
            }
        }        
    }
}
