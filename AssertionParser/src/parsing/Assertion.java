package parsing;

public class Assertion {
    public Assertion(int line, String name, String predicate) {
        this.line = line;
        this.name = name;
        this.predicate = predicate;
    }

    public int line;
    public String name;
    public String predicate;

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Assertion " + name + "(" + line + "): " + predicate;
    }
}
