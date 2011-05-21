
public class Assertion {
    public Assertion(String name, String predicate) {
        this.name = name;
        this.predicate = predicate;
    }
    
    public String name;
    public String predicate;
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Assertion " + name + ": " + predicate;
    }
}
