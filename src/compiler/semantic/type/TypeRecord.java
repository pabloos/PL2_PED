package compiler.semantic.type;

import java.util.HashMap;

import es.uned.lsi.compiler.semantic.ScopeIF;
import es.uned.lsi.compiler.semantic.symbol.SymbolIF;
import es.uned.lsi.compiler.semantic.type.TypeBase;
import es.uned.lsi.compiler.semantic.type.TypeIF;

/**
 * Class for TypeRecord.
 */

// TODO: Student work
//       Include properties to characterize records

public class TypeRecord
    extends TypeBase
{   
    // Definicion de campos del registro
    private HashMap tablaCampos = new HashMap(); 
    private int size;
           
    /**
     * Constructor for TypeRecord.
     * @param scope The declaration scope.
     */
    public TypeRecord (ScopeIF scope)
    {
        super (scope);
    }

    /**
     * Constructor for TypeRecord.
     * @param scope The declaration scope.
     * @param name The name of the type.
     */
    public TypeRecord (ScopeIF scope, String name)
    {   
        super (scope, name);
    }

    /**
     * Constructor for TypeRecord.
     * @param record The record to copy.
     */
    public TypeRecord (TypeRecord record)
    {
        super (record.getScope (), record.getName ());
    }

    // Gestion de campos del registro
    public HashMap getTablaCampos(){
        return tablaCampos;
    }
    public void setTablaCampos(HashMap tablaCampos){
        this.tablaCampos=tablaCampos;
    }
    
    public TypeIF getTypeCampo(String name){
        SymbolIF simbolo= (SymbolIF) this.getTablaCampos().get(name);
        if (simbolo != null) {
            TypeIF tipo= simbolo.getType();
            if (tipo != null && tipo instanceof TypeIF) {
                   return (TypeIF) tipo;
            } else
                return null;
        }else
                return null;
        
    }
    
    // Anyadir Campos al Registro
    public void addCampos (String name, SymbolIF simbolo)
    {
        this.getTablaCampos().put(name, simbolo);
    
    }
    public void setSize(int size){
        this.size=size;
    }
    public int getSize(){
        return this.size;
    }
    /**
     * Compares this object with another one.
     * @param other the other object.
     * @return true if both objects has the same properties.
     */
    
    public boolean containsCampo (String name)
    {
        // TODO: Student work
        return this.getTablaCampos().containsKey(name);
    }

}
