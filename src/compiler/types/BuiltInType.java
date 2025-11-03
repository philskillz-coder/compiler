package compiler.types;

// 1. Eingebaute Typen (Singular-Instanzen)
public class BuiltInType extends Type {
    public static final BuiltInType INT = new BuiltInType("int");
    public static final BuiltInType STRING = new BuiltInType("string");
    public static final BuiltInType FLOAT = new BuiltInType("float");
    public static final BuiltInType VOID = new BuiltInType("void");
    public static final BuiltInType ERROR = new BuiltInType("<error>");

    private final String name;

    private BuiltInType(String name) { this.name = name; }
    @Override public String getName() { return name; }

    // Einfache Gleichheit für Built-Ins
    @Override public boolean equals(Object other) {
        return other instanceof BuiltInType && ((BuiltInType) other).name.equals(this.name);
    }
}