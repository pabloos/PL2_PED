package compiler.syntax.nonTerminal;

import es.uned.lsi.compiler.intermediate.OperandIF;
import es.uned.lsi.compiler.semantic.type.TypeIF;

import java_cup.runtime.Symbol;
import java.util.*;

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

import compiler.semantic.utils.*;

public class Expresion extends NonTerminal {
    private TypeIF tipo;
    private OperandIF resultado;
    private int linea;
    private int columna;
    private boolean referencia = false;     // Indica si una variable puede ser de referencia.
    private int valIni;                     // Valor inicial cjto
    private int valFin;                     // Valor final   cjto

    private TemporalIF temporal; 
    private List<QuadrupleIF> code;
    
    public List<QuadrupleIF> getCode () { return code;
    }
    public void setCode (List<QuadrupleIF> code) { this.code = code;
    }
    public TemporalIF getTemporal () { return temporal;
    }
    public void setTemporal (TemporalIF temporal) { this.temporal = temporal;
    }

    // constructores
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
    
    // set y gets
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

    public int getValFin() {
        return valFin;
    }

    public void setValFin(int valFin) {
        this.valFin = valFin;
    }

    public int getValIni() {
        return valIni;
    }

    public void setValIni(int valIni) {
        this.valIni = valIni;
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
