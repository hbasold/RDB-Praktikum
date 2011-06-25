package parsing;

public class Assertion {
    public Assertion(int line, String name, String predicate) {
        this.line = line;
        this.name = name;
        this.predicate = predicate;
    }

    public Assertion(int line, String name, String predicate, boolean implemented) {
        this.line = line;
        this.name = name;
        this.predicate = predicate;
        this.implemented = implemented;
    }

    public int line;
    public String name;
    public String predicate;
    public boolean implemented;

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Assertion " + name + "(" + line + "): " + predicate;
    }
}
