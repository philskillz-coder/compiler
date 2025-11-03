package compiler.types;

public abstract class Type {
    public abstract String getName(); // Gibt den Namen des Typs zurück ("int", "MyClass")

    // Wird für Typvergleiche benötigt
    @Override
    public abstract boolean equals(Object other);
}
