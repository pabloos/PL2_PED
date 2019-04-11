package compiler.syntax.nonTerminal;

import es.uned.lsi.compiler.semantic.type.TypeIF;
import es.uned.lsi.compiler.intermediate.OperandIF;

public class Parametro extends NonTerminal {

    private String nombre;
    private Integer valor;
    private Integer linea;
    private Integer columna;
    private TypeIF tipo;
    private OperandIF resultado;
    private boolean referencia;             // parametro por referencia
    
    public Parametro() {
    }

    public Parametro(String nombre, Integer valor, Integer linea, Integer columna, TypeIF tipo) {
        this.nombre = nombre;
        this.valor = valor;
        this.linea = linea;
        this.columna = columna;
        this.tipo= tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
	
    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public Integer getLinea() {
        return linea;
    }

    public void setLinea(Integer linea) {
        this.linea = linea;
    }

    public Integer getColumna() {
        return columna;
    }

    public void setColumna(Integer columna) {
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

    @Override
    public String toString() {
        return "Nom:"+this.getNombre() + "; Valor:" + this.getValor() + "; Lin:" + this.getLinea() + "; Col:" + this.getColumna();
    }
}
