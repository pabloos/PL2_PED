package compiler.semantic.utils;

import compiler.lexical.Token;
import compiler.syntax.nonTerminal.Lista;
import compiler.syntax.nonTerminal.Parametro;
import es.uned.lsi.compiler.semantic.type.TypeIF;

import es.uned.lsi.compiler.lexical.*;
import es.uned.lsi.compiler.code.*;
import es.uned.lsi.compiler.intermediate.*;
import es.uned.lsi.compiler.semantic.*;
import es.uned.lsi.compiler.semantic.symbol.*;
import es.uned.lsi.compiler.semantic.type.*;
import es.uned.lsi.compiler.syntax.*;

import compiler.CompilerContext;
import compiler.lexical.*;
import compiler.syntax.nonTerminal.*;
import compiler.semantic.*;
import compiler.semantic.symbol.*;
import compiler.semantic.type.*;
import compiler.intermediate.*;
import compiler.code.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;


public class Utilidades {
    /** Creates a new instance of Utilidades */
    public Utilidades() {
    }

    public static String getTypeOfValue(String value) { // PARA CONSTANTES
        if(value.equals("TRUE") || value.equals("FALSE")) { // es un booleano
            return "BOOLEAN";
        } else if (value.matches("-?\\d+(\\.\\d+)?")) {   // es un entero
           return "INTEGER";
        } else {                                        // es un tipo no valido
            return "noValidType";                                       
        }
    }
    
    public static List getListFromTreeMap(TreeMap tree) {
        ArrayList list = new ArrayList();
        Iterator it = tree.values().iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }
    
     public static List getListFromHashMap(HashMap map) {
        ArrayList list = new ArrayList();
        Iterator it = map.values().iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }
    public static List ordenaParametros(Lista lista) {
        TreeMap tablaParametros = new TreeMap();

        for (int i=0; i<lista.size(); i++){
            Parametro p = (Parametro) lista.get(i);
            tablaParametros.put(p.getColumna(), p);
        }

        return getListFromTreeMap(tablaParametros);
    }
    public static TypeIF searchType(ScopeManagerIF sm, String tipoBuscado) {
        //Busca em todos los scopes a ver si existe el tipo, buscandolo a partir de su string
        
        List scopes = sm.getOpenScopes();
        Iterator it = scopes.iterator();
        Scope unScope;
        TypeTable unaTT;
        boolean found = false;
        while (it.hasNext() && !found) {
            unScope = (Scope) it.next();
            unaTT = (TypeTable) unScope.getTypeTable();
            if (unaTT.containsType(tipoBuscado)) {
                return unaTT.getType(tipoBuscado);
            }
        }
        return null;
    }

  }
