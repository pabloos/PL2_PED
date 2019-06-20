package compiler.semantic.type;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import es.uned.lsi.compiler.semantic.ScopeIF;

import compiler.semantic.symbol.SymbolVariable;
import compiler.semantic.utils.Utilidades;
import compiler.syntax.nonTerminal.Expresion;
import compiler.syntax.nonTerminal.Lista;

import es.uned.lsi.compiler.intermediate.LabelIF;
import es.uned.lsi.compiler.semantic.type.TypeBase;
import es.uned.lsi.compiler.semantic.type.TypeIF;

/**
 * Class for TypeFunction.
 */

public class TypeFunction extends TypeBase {   
    // Definicion de campos del registro
    private TreeMap tablaParametros = new TreeMap();
    private ArrayList listaParametros= new ArrayList();
    private TypeIF tipoRetorno;
    private boolean hayRetorno;
    
    /**
     * Constructor for TypeFunction.
     * @param scope The declaration scope.
     */
    public TypeFunction (ScopeIF scope) {
        super (scope);
    }

    /**
     * Constructor for TypeFunction.
     * @param scope The declaration scope
     * @param name The name of the function.
     */
    public TypeFunction (ScopeIF scope, String name) {
        super (scope, name);
    }

    public List getListaParametros(){
        return listaParametros;
    }
    public void setListaParametros(ArrayList listaParametros){
        this.listaParametros=listaParametros;
    }
    public TreeMap getTablaParametros(){
        return tablaParametros;
    }
    public void setTablaParametros(TreeMap tablaParametros){
        this.tablaParametros=tablaParametros;
    }
    
    public void setParametro(SymbolVariable par){
        this.listaParametros.add(par);
    }

    public TypeIF getTipoRetorno(){
        return this.tipoRetorno;
    }

    public void setTipoRetorno(TypeIF tretorno){
        this.tipoRetorno=tretorno;
    }
    
    public TypeIF getTypeParametro(Integer columna){
        Object tipo= this.getTablaParametros().get(columna);
        if (tipo != null && tipo instanceof TypeIF) {
            return (TypeIF) tipo;
        } else return null;
    }

    public void setTypeParametro(Integer columna, TypeIF tipo){
        this.getTablaParametros().put(columna, tipo);
    }
    
    public void addParametro (Integer columna, TypeIF type)   {
        this.getTablaParametros().put(columna, type);
    }
    
    public String getTypes ()  {    
        return this.getTablaParametros().toString();   
    }
        
    public boolean comparaParametros(Lista listaPar){
        // comprobar el numdero de parametros
        ArrayList listaArg = (ArrayList) Utilidades.getListFromTreeMap(tablaParametros);
        if (!(listaPar.size()==listaArg.size()) )  return false;
        
        // comprobar el tipo de cada parametro: La lista de parametros viene en orden inverso.
        int j=listaArg.size();
        for (int i=0; i<listaArg.size(); i++) {
            TypeIF arg = (TypeIF) listaArg.get(i);
            Expresion exp = (Expresion) listaPar.get(--j);
            TypeIF par = (TypeIF) exp.getTipo();
            if (! arg.equals(par) ) return false;
        }

        return true;
    }

    // Comprobar que hay sentencia return definida en la funcion
    public boolean getHayRetorno(){
        return this.hayRetorno;
    }

    public void setHayRetorno(boolean hayRetorno){
        this.hayRetorno=hayRetorno;
    }
}
