package parsing;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import assertionAction.AssertionAction;
import assertionAction.CheckAssertion;
import assertionAction.InsertAssertion;

/**
 * @author henning
 *
 */
public class AssertionParser {

    private Integer next;
    private int line;
    private int column;

    private class BackTrack {
        private int line;
        private int column;
        private Queue<Integer> buffer = new LinkedList<Integer>();

        public BackTrack(int line, int column) {
            this.line = line;
            this.column = column;
        }

        public int line(){
            return line;
        }

        public int column(){
            return column;
        }

        public boolean empty(){
            return buffer.isEmpty();
        }

        public Integer get(){
            return buffer.poll();
        }

        public void put(Integer c){
            buffer.add(c);
        }
    }

    private BackTrack readBacktrack;
    private BackTrack writeBacktrack;

    Pattern identifierPattern;

    public AssertionParser(){
        identifierPattern = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");

        readBacktrack = null;
        writeBacktrack = null;
    }

    private void startAlternative(){
        writeBacktrack = new BackTrack(line, column);
    }

    private void backtrackAlternative(){
        readBacktrack = writeBacktrack;
        line = readBacktrack.line();
        column = readBacktrack.column();
        writeBacktrack = null;
    }

    private void endAlternative(){
        writeBacktrack = null;
    }

    public Either<String, Vector<AssertionAction>> parse(InputStreamReader input) {
        Vector<AssertionAction> asserts = new Vector<AssertionAction>();

        InputStreamIterator in = new InputStreamIterator(input);
        next = null;
        line = 1;
        column = 0;

        if(hasNext(in)){
            nextChar(in);
        }
        else{
            return Either.Left("empty input").get();
        }

        while(hasNext(in)){
            Either<Vector<AssertionParseError>, AssertionAction> act = parseAction(in);
            if(act.isLeft()){
                String errorText = "error while parsing alternatives :";
                for(AssertionParseError e : act.left()){
                    errorText = errorText + "\n" +  e.getMessage();
                }
                return Either.Left(errorText).get();
            }
            else{
                asserts.add(act.right());
            }
            skipWS(in);
        }

        return Either.Right(asserts).get();
    }

    private Either<Vector<AssertionParseError>, AssertionAction> parseAction(InputStreamIterator in){
        AssertionAction action = null;
        Vector<AssertionParseError> errors = new Vector<AssertionParseError>();

        startAlternative();

        try {
            action = parseAssertion(in);
        }
        catch (AssertionParseError e) {
            errors.add(e);
        }

        if(action == null){
            backtrackAlternative();
            startAlternative();
            try {
                action = parseCheckAssertion(in);
            }
            catch (AssertionParseError e) {
                errors.add(e);
            }

            if(action == null){
                backtrackAlternative();
                startAlternative();
                try {
                    action = parseCheckAssertion(in);
                }
                catch (AssertionParseError e) {
                    errors.add(e);
                }
                if(action == null){
                    return Either.Left(errors).get();
                }
            }
            else{
                endAlternative();
            }
        }
        else{
            endAlternative();
        }

        return Either.Right(action).get();
    }

    private AssertionAction parseCheckAssertion(InputStreamIterator in) throws AssertionParseError {
        skipWS(in);

        int startLine = line;

        parseLiteral("CHECK", in);

        skipWS(in);
        parseLiteral("ASSERTION", in);

        skipWS(in);

        String identifier = parseIndentifier(in);

        skipWS(in);
        parseLiteral(";", in);

        return new CheckAssertion(startLine, identifier);
    }

    /**
     * @param in
     * @return
     * @throws AssertionParseError
     */
    private AssertionAction parseAssertion(InputStreamIterator in) throws AssertionParseError {
        skipWS(in);

        int startLine = line;

        parseLiteral("CREATE", in);

        skipWS(in);
        parseLiteral("ASSERTION", in);

        skipWS(in);

        String identifier = parseIndentifier(in);

        skipWS(in);
        parseLiteral("CHECK", in);

        skipWS(in);
        parseLiteral("(", in);

        skipWS(in);

        String predicate = parsePredicate(in);

        skipWS(in);
        parseLiteral(";", in);

        return new InsertAssertion(new Assertion(startLine, identifier, predicate));
    }

    private void skipWS(InputStreamIterator in) {
        if(!isWS(next)){
            return;
        }

        while(hasNext(in)){
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

    private void parseLiteral(String toParse, InputStreamIterator in) throws AssertionParseError {
        if(next == null){
            throw new AssertionParseError(toParse, "", "", line, column);
        }

        int pos = 0;
        StringBuffer read = new StringBuffer();

        read.appendCodePoint(next.intValue());
        if(!isCaseInsensitveEqual(toParse.codePointAt(pos), next.intValue())){
            throw new AssertionParseError(toParse, read.toString(), context(read.toString(), in), line, column);
        }
        ++pos;

        while(pos < toParse.length() && hasNext(in)) {
            nextChar(in);
            read.appendCodePoint(next.intValue());

            if(!isCaseInsensitveEqual(toParse.codePointAt(pos), next.intValue())){
                throw new AssertionParseError(toParse, read.toString(), context(read.toString(), in), line, column);
            }
            ++pos;
        }

        if(hasNext(in)){
            nextChar(in);
        }
    }

    /**
     * Nur zur Benutzung in Falle eines Parsefehlers.
     *
     * Liest bis zum nächsten Leerzeichen.
     *
     * @param soFar
     * @param in
     * @return
     */
    private String context(String soFar, InputStreamIterator in) {
        StringBuffer read = new StringBuffer();
        while(!isWS(next) && hasNext(in)) {
            nextChar(in);
            read.appendCodePoint(next.intValue());
        }

        return soFar + read.toString();
    }

    private boolean isCaseInsensitveEqual(int a, int b) {
        return Character.toLowerCase(a) == Character.toLowerCase(b);
    }

    private String parseIndentifier(InputStreamIterator in) throws AssertionParseError {
        int oldLine = line;
        int oldColumn = column;

        StringBuffer word = new StringBuffer();

        word.appendCodePoint(next.intValue());
        Matcher idMatcher = identifierPattern.matcher(word);

        if(!idMatcher.matches()){
            throw new AssertionParseError("<identifier>", next+"", context(word.toString(), in), oldLine, oldColumn);
        }

        while(hasNext(in) && idMatcher.matches()) {
            nextChar(in);
            word.appendCodePoint(next.intValue());
            idMatcher.reset(word);
        }

        // letztes Zeichen, das nicht getroffen hat, gehört nicht zum Indentifier
        return word.substring(0, word.length() - 1);
    }

    private String parsePredicate(InputStreamIterator in) throws AssertionParseError {
        StringBuffer predicate = new StringBuffer();
        boolean predComplete = false;

        if(next.intValue() != ';'){
            predicate.appendCodePoint(next.intValue());
        }

        while(!predComplete && hasNext(in)){
            nextChar(in);

            if(next.intValue() == ';'){
                predComplete = true;
            }
            else{
                predicate.appendCodePoint(next.intValue());
            }
        }

        if(!hasNext(in) && !predComplete){
            predicate.appendCodePoint(next.intValue());
            throw new AssertionParseError(";", predicate.charAt(predicate.length())+"", predicate.toString(), line, column);
        }

        String p = predicate.toString().trim();
        if(p.isEmpty()){
            throw new AssertionParseError("<predicate>", p, predicate.toString(), line, column);
        }
        else if(p.codePointAt(p.length() - 1) != ')'){
            throw new AssertionParseError(")", p.codePointAt(p.length() - 1)+"", p, line, column);
        }
        else{
            // FIXME: stimmt Indizierung? es wird [start, end[ zurückgegeben -> müsste length-1 sein.
            String p_ = p.substring(0, p.length() - 2).trim();
            if(p_.isEmpty()){
                throw new AssertionParseError("<predicate>", p_, predicate.toString(), line, column);
            }
            else{
                return p_;
            }
        }
    }

    private boolean hasNext(InputStreamIterator in){
        if(readBacktrack != null && !readBacktrack.empty()){
            return true;
        }
        else{
            return in.hasNext();
        }
    }

    /**
     * Geht zum nächsten Zeichen und aktualisiert die Position entsprechend.
     *
     * @param in
     */
    private void nextChar(InputStreamIterator in) {
        // Das aktuelle Zeichen muss noch mit aufgenommen werden,
        // da das Backtracking erst gestartet werden kann,
        // nachdem bereits ein Zeichen gelesen wurde.
        if(writeBacktrack != null){
            writeBacktrack.put(next);
        }

        if(readBacktrack != null && readBacktrack.empty()){
            readBacktrack = null;
        }

        if(readBacktrack == null){
            next = in.next();
        }
        else{
            next = readBacktrack.get();
        }

        if(isLineBreak(next)){
            column = 0;
            ++line;
        }
        ++column;
    }
}
