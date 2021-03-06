package compiler.semantic.type;

import java.util.HashMap;

import es.uned.lsi.compiler.semantic.ScopeIF;
import es.uned.lsi.compiler.semantic.symbol.SymbolIF;
import es.uned.lsi.compiler.semantic.type.TypeBase;
import es.uned.lsi.compiler.semantic.type.TypeIF;


/**
 * Class for TypeRecord.
 */

public class TypeRecord
    extends TypeBase
{   
    // Definicion de campos del registro
    private HashMap<String, SymbolIF> tablaCampos = new HashMap<String, SymbolIF>(); 
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

    public HashMap<String, SymbolIF> getTablaCampos(){
        return tablaCampos;
    }
    
    public void setTablaCampos(HashMap<String, SymbolIF> tablaCampos){
        this.tablaCampos=tablaCampos;
    }
    
    public void addCampos (String name, SymbolIF simbolo){
        this.tablaCampos.put(name, simbolo);
    
    }
    public void setSize(int size){
        this.size = size;
    }
    public int getSize(){
        return this.size;
    }
    
    public TypeIF getTypeCampo(String name){
        SymbolIF simbolo = (SymbolIF) this.getTablaCampos().get(name);
        
        return simbolo.getType();
    }

    /**
     * Compares this object with another one.
     * @param other the other object.
     * @return true if both objects has the same properties.
     */
    
    public boolean containsCampo (String name) {
        return this.tablaCampos.containsKey(name);
    }
}
