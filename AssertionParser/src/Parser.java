import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author henning
 *
 */
public class Parser {
    
    Integer next;
    int line;
    int column;
    
    Pattern identifierPattern;
    
    Parser(){
        identifierPattern = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
    }
    
    public Vector<Assertion> parse(InputStreamReader input) throws ParseError {
        Vector<Assertion> asserts = new Vector<Assertion>();
        
        InputStreamIterator in = new InputStreamIterator(input);
        next = null;
        line = 0;
        column = 0;
        
        if(in.hasNext()){
            nextChar(in);
        }
        else{
            throw new ParseError("Empty input", line, column);
        }
        
        while(in.hasNext()){
            asserts.add(parseAssertion(in));
            skipWS(in);
        }
        
        return asserts;
    }

    /**
     * @param in
     * @return 
     * @throws ParseError
     */
    private Assertion parseAssertion(InputStreamIterator in) throws ParseError {
        skipWS(in);
        parseLiteral("CREATE", in);
        
        skipWS(in);        
        parseLiteral("ASSERTION", in);
        
        skipWS(in);
        
        String identifier;
        if(next != null){
            identifier = parseIndentifier(in);
        }
        else {
            throw new ParseError("Expected indentifier", line, column);
        }
        
        skipWS(in);        
        parseLiteral("CHECK", in);
        
        skipWS(in);
        parseLiteral("(", in);
        
        skipWS(in);
        
        String predicate;
        if(next != null){
            predicate = parsePredicate(in);
        }
        else {
            throw new ParseError("Expected predicate", line, column);
        }
        
        skipWS(in);        
        parseLiteral(";", in);
        
        return new Assertion(identifier, predicate);
    }

    private void skipWS(InputStreamIterator in) {
        if(!isWS(next)){
            return; 
        }
        
        while(in.hasNext()){
            nextChar(in);
            
            if(!isWS(next)){
                return; 
            }
        }
        
        next = null; // kein Zeichen nach den Whitespaces
    }

    private boolean isLineBreak(Integer c) {
        return c.intValue() == '\n'
                || c.intValue() == '\r'
                || Character.getType(c.intValue()) == Character.LINE_SEPARATOR;
    }

    private boolean isWS(Integer next) {
        return Character.isWhitespace(next.intValue());
    }
    
    private void parseLiteral(String toParse, InputStreamIterator in) throws ParseError {
        if(next == null){
            throw new ParseError("Expected token \"" + toParse + "\"", line, column);
        }
        
        int pos = 0;
        StringBuffer read = new StringBuffer();
        
        read.appendCodePoint(next.intValue());        
        if(!isCaseInsensitveEqual(toParse.codePointAt(pos), next.intValue())){
            throw new ParseError("Expected token \"" + toParse + "\" but got \"" + read + "\"", line, column);
        }
        ++pos;
        
        while(pos < toParse.length() && in.hasNext()) {
            nextChar(in);
            read.appendCodePoint(next.intValue());
            
            if(!isCaseInsensitveEqual(toParse.codePointAt(pos), next.intValue())){
                throw new ParseError("Expected token \"" + toParse + "\" but got \"" + read + "\"", line, column);
            }
            ++pos;
        }
        
        if(in.hasNext()){
            nextChar(in);
        }
    }

    private boolean isCaseInsensitveEqual(int a, int b) {
        return Character.toLowerCase(a) == Character.toLowerCase(b);
    }
    
    private String parseIndentifier(InputStreamIterator in) throws ParseError {
        int oldLine = line;
        int oldColumn = column;
        
        StringBuffer word = new StringBuffer();

        if(!isWS(next)){
            word.appendCodePoint(next.intValue());
        }
        
        while(in.hasNext()) {
            nextChar(in);
            if(!isWS(next)){
                word.appendCodePoint(next.intValue());
            }
            else{
                break;
            }
        }
        
        Matcher idMatcher = identifierPattern.matcher(word);
        if(idMatcher.matches()){
            return word.toString();
        }
        else{
            throw new ParseError("Expected identifier but got \"" + word + "\"", oldLine, oldColumn);
        }
    }
    
    private String parsePredicate(InputStreamIterator in) throws ParseError {
        StringBuffer predicate = new StringBuffer();
        boolean predComplete = false;
        
        if(next.intValue() != ';'){
            predicate.appendCodePoint(next.intValue());
        }
        
        while(!predComplete && in.hasNext()){
            nextChar(in);
            
            if(next.intValue() == ';'){
                predComplete = true;
            }
            else{
                predicate.appendCodePoint(next.intValue());
            }
        }
        
        if(!in.hasNext() && !predComplete){
            throw new ParseError("Expected \";\"", line, column);
        }
        
        String p = predicate.toString().trim();
        if(p.isEmpty()){
            throw new ParseError("Expected non empty predicate", line, column);
        }
        else if(p.codePointAt(p.length() - 1) != ')'){
            throw new ParseError("Expected \")\"", line, column);
        }
        else{
            return p.substring(0, p.length() - 2).trim();
        }
    }

    /**
     * Geht zum nächsten Zeichen und aktualisiert die Position entsprechend.
     * 
     * @param in
     */
    private void nextChar(InputStreamIterator in) {
        next = in.next();
        
        if(isLineBreak(next)){
            column = 0;
            ++line;
        }
        ++column;
    }
}
