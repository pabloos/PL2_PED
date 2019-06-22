package compiler.semantic.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import compiler.syntax.nonTerminal.Lista;
import compiler.syntax.nonTerminal.Parametro;

public class Utilidades {
    /** Creates a new instance of Utilidades */
    public Utilidades() {
    }

    public static String getTypeOfValue(String value) { // PARA CONSTANTES
        if(value.toUpperCase().equals("TRUE") || value.toUpperCase().equals("FALSE")) { // es un booleano
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
  }
