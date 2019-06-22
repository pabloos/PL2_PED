package compiler.semantic.symbol;

import es.uned.lsi.compiler.semantic.ScopeIF;
import es.uned.lsi.compiler.semantic.symbol.SymbolBase;
import es.uned.lsi.compiler.semantic.type.TypeIF;

/**
 * Class for SymbolVariable.
 */

public class SymbolVariable
    extends SymbolBase
{  
    private int desplazamiento;
    private boolean referencia;

    /**
     * Constructor for SymbolVariable.
     * @param scope The declaration scope.
     * @param name The symbol name.
     * @param type The symbol type.
     */
    public SymbolVariable (ScopeIF scope, 
                           String name,
                           TypeIF type)
    {
        super (scope, name, type);
    }
    public SymbolVariable (ScopeIF scope, 
                           String name,
                           TypeIF type, 
                           int desplazamiento)
    {
        super (scope, name, type);
        this.desplazamiento=desplazamiento;
    }        

    public boolean getReferencia() {
        return referencia;
    }

    public void setReferencia(boolean referencia) {
        this.referencia = referencia;
    }
    
    public int getDesplazamiento() {
        return this.desplazamiento;
    }

    public void setDesplazamiento(int desplazamiento) {
        this.desplazamiento = desplazamiento;
    }
    
}

