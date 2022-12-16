package critterscript.interpreter;

public class Variable {
    private final String name;
    private Object value;
    private final boolean isConstant;

    public Variable(String name, Object value, boolean isConstant) {
        this.name = name;
        this.value = value;
        this.isConstant = isConstant;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
