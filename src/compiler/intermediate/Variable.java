package compiler.intermediate;

import java.util.HashMap;

import es.uned.lsi.compiler.intermediate.VariableIF;
import es.uned.lsi.compiler.semantic.ScopeIF;
import es.uned.lsi.compiler.intermediate.LabelIF;

/**
 * Class for variables in intermediate code.
 */

public class Variable
    implements VariableIF 
{
    String  name  = null;
    ScopeIF scope = null;
    private HashMap tablaDespl = new HashMap(); 
    private LabelIF etiqRetorno;
    private LabelIF etiqSub;
    private int desplCampo;   // Utilizada unicamente para registros
    private ScopeIF ambito;

    /**
     * Constructor for Variable.
     * @param name The name.
     */
    public Variable (String name)
    {
        this.name = name;
    }
    
    /**
     * Constructor for Variable.
     * @param name The name.
     * @param scope The scope index.
     */
    public Variable (String name, ScopeIF scope)
    {
        this (name);
        this.scope = scope;
    }

    /**
     * Returns the name.
     * @return Returns the name.
     */
    public String getName ()
    {
        return name;
    }

    /**
     * Sets The name.
     * @param name The name to set.
     */
    public void setName (String name)
    {
        this.name = name;
    }


    /**
     * Returns the scope.
     * @return Returns the scope.
     */
    public ScopeIF getScope ()
    {
        return scope;
    }

    /**
     * Sets The scope.
     * @param scope The scope to set.
     */
    public void setScope (ScopeIF scope)
    {
        this.scope = scope;
    }
    
    /**
     * Compares this object with another one.
     * @param other the other object.
     * @return true if both objects has the same properties.
     */
    public boolean equals (Object other)
    {
        // TODO: Student Work (optional)
        if (other == null) 
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof Variable))
        {
            return false;
        }
        
        final Variable aVariable = (Variable) other;
        return ((scope != null) ? (scope == aVariable.scope) : aVariable.scope == null) && 
               ((name  != null) ? (name  == aVariable.name)  : aVariable.name  == null);
    }


    // Gestion de tabla desplaz.
    public HashMap getTablaDespl(){
        return tablaDespl;
    }
    public void setTablaDespl(HashMap tablaDespl){
        this.tablaDespl=tablaDespl;
    }
    public int getDesplCampo() {
        return desplCampo;
    }

    public void setDesplCampo(int desplCampo) {
        this.desplCampo = desplCampo;
    }

    public ScopeIF getAmbito() {
        return ambito;
    }

    public void setAmbito(ScopeIF ambito) {
        this.ambito = ambito;
        
    }
   
    /**
     * Return a string representing the object.
     * @return a string representing the object.
     */
    public String toString ()
    {    
        // TODO: Student Work (optional)
        return name;
    }

    @Override
    public int getAddress() {
        return 0;
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
