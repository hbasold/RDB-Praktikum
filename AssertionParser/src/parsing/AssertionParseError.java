package parsing;


/**
 * Wird geworfen, wenn während des Parsens ein Fehler auftritt.
 */

public class AssertionParseError extends Exception {
    private static final long serialVersionUID = -6664634894793916509L;

    private String expected;
    private String found;
    private String context;
    private int line;
    private int column;

    public AssertionParseError(String expected, String found, String context, int line, int column) {
        this.expected = expected;
        this.found = found;
        this.context = context;
        this.line = line;
        this.column = column;
    }

    @Override
    public String getMessage(){
        return
            "Expected \"" + expected
            + "\" but got \"" + found
            + "\" at " + line + ":" + column
            + ". Context: \"" + context + "\"";
    }
}