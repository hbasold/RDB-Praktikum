

/**
 * 
 */

public class ParseError extends Exception {
    private static final long serialVersionUID = -6664634894793916509L;
    
    private String what;
    private int line;
    private int column;

    public ParseError(String what_, int line_, int column_) {
        what = what_;
        line = line_;
        column = column_;
    }

    @Override
    public String getMessage(){
        return "Parse error at " + line + ":" + column + ": " + what; 
    }
}