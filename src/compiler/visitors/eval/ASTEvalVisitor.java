package compiler.visitors.eval;

import compiler.parser.Modifier;
import compiler.parser.ast.*;
import compiler.visitors.ASTVisitor;
import compiler.visitors.eval.values.*;
import compiler.visitors.eval.values.complex.ClassValue;
import compiler.visitors.eval.values.complex.FunctionValue;
import compiler.visitors.eval.values.complex.ObjectValue;
import compiler.visitors.eval.values.literal.*;
import compiler.visitors.eval.exceptions.EvalException;
import compiler.visitors.eval.values.memory.ClassClosure;
import compiler.visitors.eval.values.memory.Closure;
import compiler.visitors.eval.values.memory.ObjectClosure;
import compiler.visitors.eval.values.memory.Variable;

import java.util.*;

public class ASTEvalVisitor implements ASTVisitor<EvalResult> {

    private final Deque<Closure> closures = new ArrayDeque<>();

    public ASTEvalVisitor() {
        Closure root = new Closure();
        root.defineHere("print", new Variable(new FunctionValue(new Print(), root))); // todo: final or not

        closures.push(root);
    }

    private void enterClosure() { closures.push(new Closure(closures.peek())); }
    private void enterClosure(Closure closure) { closures.push(closure); }
    private void exitClosure() { closures.pop(); }

    private void define(String name, Variable var) {
        assert closures.peek() != null;
        closures.peek().defineHere(name, var);
    }
    private void reassign(String name, AbstractValue value) {
        assert closures.peek() != null;
        closures.peek().reassign(name, value);
    }

    private AbstractValue resolve(String name) {
        assert closures.peek() != null;
        AbstractValue value = closures.peek().getValueLookup(name);
        if (value != null) return value;
        throw new EvalException("Variable not defined: " + name);
    }

    @Override
    public EvalResult visitProgram(Program node) {
        EvalResult last = EvalResult.nullValue();
        for (Stmt stmt : node.statements) {
            last = stmt.accept(this);
            if (last.isBreaking()) return last;
        }
        return last;
    }

    @Override
    public EvalResult visitBlock(Block node) {
        if (node instanceof VirtualBlockExpr) {
            return ((VirtualBlockExpr) node).execute(closures.peek());
        }

        enterClosure();
        try {
            EvalResult last = EvalResult.nullValue();
            for (Stmt stmt : node.statements) {
                last = stmt.accept(this);
                if (last.isBreaking()) break;
            }
            return last;
        } finally {
            exitClosure();
        }
    }

    @Override
    public EvalResult visitVariableDecl(VariableDecl node) {
        define(node.name, new Variable(NullValue.getInstance(), node.modifiers));
        return EvalResult.nullValue();
    }

    @Override
    public EvalResult visitVariableDef(VariableDef node) {
        EvalResult value = node.initialValue.accept(this);
        define(node.name, new Variable(value.unwrapValue(), node.modifiers));
        return value;
    }

    @Override
    public EvalResult visitAssign(AssignExpr node) {
        EvalResult value = node.value.accept(this);
        AbstractValue rawValue = value.unwrapValue();

        if (node.target instanceof VariableExpr) { // simple variable assignment
            reassign(((VariableExpr) node.target).name, rawValue);
        } else if (node.target instanceof FieldAccessExpr) { // field assignment
            FieldAccessExpr fieldNode = (FieldAccessExpr) node.target;

            // Das Ziel-Objekt auswerten (die linke Seite vom Punkt)
            EvalResult targetResult = fieldNode.target.accept(this);

            if (!targetResult.isObject()) {
                throw new EvalException("Cannot assign to field on non-object");
            }

            ObjectClosure objectClosure = targetResult.asObject().getClosure();

            // Hier nutzt du jetzt deine intelligente setValue-Methode aus der ObjectClosure!
            // Sie entscheidet selbst: Existiert das Feld statisch in der Klasse?
            // Wenn ja → dort ändern. Wenn nein → lokal im Objekt setzen.
            objectClosure.setValueObject(fieldNode.fieldName, rawValue);
        } else {
            throw new EvalException("Invalid assignment target");
        }

        return value;
    }

    @Override
    public EvalResult visitVariableExpr(VariableExpr node) {
        return EvalResult.value(resolve(node.name));
    }

    @Override
    public EvalResult visitFieldAccessExpr(FieldAccessExpr node) {
        // 1. Die linke Seite auswerten (z.B. die Variable 'auto')
        EvalResult targetResult = node.target.accept(this);

        if (targetResult.isObject()) {
            // 3. Die Closure aus dem ObjectValue holen
            ObjectClosure objectClosure = targetResult.asObject().getClosure();

            // 4. Den Wert suchen (Nutzt deine rekursive getValueParent Logik)
            AbstractValue value = objectClosure.getValueObject(node.fieldName);

            if (value == null) {
                throw new EvalException("Field '" + node.fieldName + "' is not defined.");
            }

            // bind object closure
            if (value instanceof FunctionValue) {
                FunctionValue staticMethod = (FunctionValue) value;
                // Wir "binden" die Methode an das aktuelle Objekt,
                // indem wir die ObjectClosure als neuen Heimat-Scope setzen.
                return EvalResult.value(new FunctionValue(staticMethod.getDecl(), objectClosure));
            }

            return EvalResult.value(value);
        }
        if (targetResult.isClass()) {
            ClassClosure classClosure = targetResult.asClass().getClosure();
            AbstractValue value = classClosure.getValueClass(node.fieldName);

            if (value == null) {
                throw new EvalException("Class '" + node.fieldName + "' is not defined.");
            }

            return EvalResult.value(value);
        }

        throw new EvalException("Property access on non-object: " + node.target);
    }

    @Override
    public EvalResult visitFunctionDecl(FunctionDecl node) {
        define(node.name, new Variable(new FunctionValue(node, closures.peek()), node.modifiers));

        return EvalResult.nullValue();
    }

    @Override
    public EvalResult visitFunctionCall(FunctionCall node) {
        // 1. Zuerst die Funktion suchen, SOLANGE wir noch im alten Scope sind!
        EvalResult callee = node.callee.accept(this);

        if (callee.isFunction()) {
            FunctionDecl func = callee.asFunction().getDecl();
            Closure definitionClosure = callee.asFunction().getClosure();

            if (func.parameters.size() != node.arguments.size()) {
                throw new EvalException("Argument count mismatch for function " + func.name); // todo: kw arguments
            }

            // 2. Argumente auswerten, SOLANGE wir noch im alten Scope sind!
            // (Sonst könnten Argumente keine Variablen von "außen" lesen)
            List<AbstractValue> evaluatedArgs = new ArrayList<>();
            for (Expr arg : node.arguments) {
                evaluatedArgs.add(arg.accept(this).unwrapValue());
            }

            // 3. Jetzt erst in den neuen Scope wechseln
            Closure callClosure = new Closure(definitionClosure);
            enterClosure(callClosure);

            // 4. Parameter in der neuen Closure zuweisen
            for (int i = 0; i < func.parameters.size(); i++) {
                define(func.parameters.get(i).name, new Variable(evaluatedArgs.get(i)));
            }

            // 5. Body ausführen
            EvalResult result = func.body.accept(this);
            exitClosure();

            if (result.is(EvalResult.ResultType.RETURN)) {
                AbstractValue returnVal = result.unwrapReturnValue();
                return EvalResult.value(returnVal != null ? returnVal : NullValue.getInstance());
            }
            return result;
        }
        else if (callee.isClass()) {
            ClassValue classVal = callee.asClass();
            ClassDecl decl = classVal.getDecl();

            // 1. Das leere ObjectValue erstellen (die Hülle)
            ObjectValue instance = new ObjectValue();

            // 2. Die ObjectClosure erstellen und mit der Hülle verknüpfen
            // Hier wird 'this' automatisch in der Closure registriert!
            ObjectClosure instanceScope = new ObjectClosure(classVal.getClosure(), instance);
            instance.setClosure(instanceScope);

            // 3. Instanz-Felder mit Default-Werten initialisieren
            for (VariableDecl field : decl.fields) { // todo: keine ahnung ob das gut ist
                if (field.modifiers.contains(Modifier.STATIC)) { // überspringe statische attribute
                    continue;
                }
                // Wir nutzen instanceScope.setValue, damit isStatic=false gesetzt wird
                if (field instanceof VariableDef) {
                    AbstractValue initialValue = ((VariableDef) field).initialValue.accept(this).unwrapValue();
                    instanceScope.defineHere(field.name, new Variable(initialValue, field.modifiers));
                } else {
                    instanceScope.defineHere(field.name, new Variable(NullValue.getInstance(), field.modifiers));
                }
            }

            // 4. Argumente für den Konstruktor auswerten
            List<AbstractValue> args = new ArrayList<>();
            for (Expr arg : node.arguments) {
                args.add(arg.accept(this).unwrapValue());
            }

            // 5. Den Konstruktor suchen (nur innerhalb der Klassenhierarchie!)
            // Wir nutzen getValue, um nicht versehentlich globale Funktionen zu finden
            ClassClosure cc = classVal.getClosure();
            if (cc.existsHere("constructor")) {
                AbstractValue constructor = classVal.getClosure().getValueClass("constructor");

                if (constructor instanceof FunctionValue) {
                    FunctionValue constructorFunc = (FunctionValue) constructor;
                    FunctionDecl funcDecl = constructorFunc.getDecl();

                    // Check: Passen die Argumente?
                    if (funcDecl.parameters.size() != args.size()) {
                        throw new EvalException("Constructor of " + decl.name +
                                " expects " + funcDecl.parameters.size() + " arguments.");
                    }

                    // 6. Konstruktor-Scope bauen
                    // WICHTIG: Parent ist die ObjectClosure, damit 'this' gefunden wird!
                    Closure callScope = new Closure(instanceScope);

                    // Parameter binden
                    for (int i = 0; i < funcDecl.parameters.size(); i++) {
                        callScope.defineHere(funcDecl.parameters.get(i).name, new Variable(args.get(i)));
                    }

                    // 7. Ausführen
                    enterClosure(callScope);
                    try {
                        funcDecl.body.accept(this);
                    } finally {
                        exitClosure();
                    }
                }
            }

            // Rückgabe ist immer das neue Objekt
            return EvalResult.value(instance);
        }
        throw new EvalException("Callee is not a function or class constructor");
    }

    @Override
    public EvalResult visitClassDecl(ClassDecl node) {
        // 1. Erstelle die ClassClosure (der statische Scope der Klasse)
        // Ihr Parent ist die aktuelle Umgebung (z.B. root), damit die Klasse
        // globale Funktionen/Variablen sieht.
        ClassClosure classScope = new ClassClosure(closures.peek());

        // 2. Methoden registrieren
        for (FunctionDecl method : node.methods) {
            // Jede Methode bekommt die ClassClosure als "Heimat-Scope" (Rucksack).
            // Dadurch finden Methoden später statische Felder über getValueParent.
            FunctionValue funcValue = new FunctionValue(method, classScope);
            classScope.defineHere(method.name, new Variable(funcValue, method.modifiers));
        }

        // 3. Statische Felder (VariableDecl) initialisieren
        for (VariableDecl field : node.fields) {
            if (!field.modifiers.contains(Modifier.STATIC)) { // überspringe nicht-statische felder
                continue;
            }
            if (field instanceof VariableDef) {
                AbstractValue initialValue = ((VariableDef) field).initialValue.accept(this).unwrapValue();
                classScope.defineHere(field.name, new Variable(initialValue, field.modifiers));
            } else {
                classScope.defineHere(field.name, new Variable(NullValue.getInstance(), field.modifiers));
            }
        }

        // 4. Innere Klassen (rekursiv)
        // Falls dein System das unterstützt, einfach die visit-Logik darauf anwenden
        // (Diese landen dann als statische Member in der aktuellen ClassClosure)
        enterClosure(classScope); // todo: keine ahnung
        for (ClassDecl inner : node.innerClasses) {
            inner.accept(this);
        }
        exitClosure();

        // 5. Die Klasse selbst als "Typ-Objekt" im aktuellen Scope registrieren
        // Wir packen die ClassClosure in einen speziellen Value-Typ (z.B. ClassValue),
        // damit der 'new'-Operator später darauf zugreifen kann.
        define(node.name, new Variable(new ClassValue(node, classScope), node.modifiers));

        return EvalResult.nullValue();
    }

    @Override
    public EvalResult visitLiteralInt(LiteralInt node) { return EvalResult.fromInt(node.value); }
    @Override
    public EvalResult visitLiteralFloat(LiteralFloat node) { return EvalResult.fromFloat(node.value); }
    @Override
    public EvalResult visitLiteralBool(LiteralBool node) { return EvalResult.fromBool(node.value); }
    @Override
    public EvalResult visitLiteralString(LiteralString node) { return EvalResult.fromString(node.value); }

    @Override
    public EvalResult visitBinaryOp(BinaryOp node) {
        EvalResult left = node.lhs.accept(this);
        EvalResult right = node.rhs.accept(this);
        return left.applyBinary(node.op, right);
    }

    @Override
    public EvalResult visitUnaryOp(UnaryOp node) {
        EvalResult operand = node.value.accept(this);
        return operand.applyUnary(node.op);
    }

    @Override
    public EvalResult visitIf(IfStmt node) {
        EvalResult cond = node.condition.accept(this);
        if ((boolean) cond.asBoolean().getNativeAbstractValue()) return node.thenBranch.accept(this);
        if (node.elseBranch != null) return node.elseBranch.accept(this);
        return EvalResult.nullValue();
    }

    @Override
    public EvalResult visitWhile(WhileStmt node) {
        EvalResult last = EvalResult.nullValue();
        while ((boolean) node.condition.accept(this).asBoolean().getNativeAbstractValue()) {
            last = node.body.accept(this);
            if (last.isBreaking()) break;
        }
        return last;
    }

    @Override
    public EvalResult visitReturn(ReturnStmt node) {
        if (node.returnValue != null) return EvalResult.returnValue(node.returnValue.accept(this).unwrapValue());
        return EvalResult.returnValue(null);
    }

    @Override
    public EvalResult visitExpr(ExprStmt node) {
        return node.expr.accept(this);
    }
}
