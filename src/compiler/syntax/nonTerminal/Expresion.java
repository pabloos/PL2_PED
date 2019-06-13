package compiler.syntax.nonTerminal;

import es.uned.lsi.compiler.intermediate.OperandIF;
import es.uned.lsi.compiler.semantic.type.TypeIF;

import java.util.*;

import es.uned.lsi.compiler.intermediate.*;


public class Expresion extends NonTerminal {
    private TypeIF tipo;
    private OperandIF resultado;
    private int linea;
    private int columna;
    private boolean referencia = false;     // Indica si una variable puede ser de referencia.

    private TemporalIF temporal; 
    private List<QuadrupleIF> code;
    
    public List<QuadrupleIF> getCode () { 
        return code;
    }

    public void setCode (List<QuadrupleIF> code) { 
        this.code = code;
    }

    public TemporalIF getTemporal () { 
        return temporal;
    }

    public void setTemporal (TemporalIF temporal) { 
        this.temporal = temporal;
    }

    public Expresion(TypeIF tipo, int linea, int columna) {
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }
    
    public Expresion(TypeIF tipo) {
        this.tipo = tipo;
    }
    
    public Expresion() {
    }
    
    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }
    
     public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public TypeIF getTipo() {
        return tipo;
    }

    public void setTipo(TypeIF tipo) {
        this.tipo = tipo;
    }
    
    public OperandIF getResultado() {
        return resultado;
    }

    public void setResultado(OperandIF resultado) {
        this.resultado = resultado;
    }

    public boolean getReferencia() {
        return referencia;
    }

    public void setReferencia(boolean referencia) {
        this.referencia = referencia;
    }
    
    // Conversi�n de tipos
    public boolean castingTipos(Expresion exp1, Expresion exp2){
        boolean error = true;
        
        if ( (exp1.tipo.getName().equals("INTEGER") &&  exp2.tipo.getName().equals("POINTER"))) {    		
            error = false;
    	} else if ( (exp1.tipo.getName().equals("POINTER") && exp2.tipo.getName().equals("INTEGER"))) {
            error = false;
    	} else if ( (exp1.tipo.getName().equals("POINTER") && exp2.tipo.getName().equals("POINTER"))) {
            error = false;
    	} else if ((exp1.tipo.getName().equals("INTEGER") && exp2.tipo.getName().equals("INTEGER"))) {
    		error = false;
        } else {
            error = true;
    	}
        return error;     	
    }
}
