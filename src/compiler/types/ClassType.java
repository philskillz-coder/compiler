package compiler.types;

public class ClassType extends Type { // classes are not implemented yet
    private final String className;

    public ClassType(String className) {
        this.className = className;
    }

    @Override public String getName() { return className; }

    @Override public boolean equals(Object other) {
        return other instanceof ClassType && ((ClassType) other).className.equals(this.className);
    }
}
