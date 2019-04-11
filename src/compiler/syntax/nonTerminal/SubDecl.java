package compiler.syntax.nonTerminal;

public class SubDecl extends NonTerminal {
    private boolean esFuncion;

    public SubDecl(boolean esFuncion) {
        this.esFuncion = esFuncion;
    }

    public boolean getEsFuncion() {
        return this.esFuncion;
    }
}
