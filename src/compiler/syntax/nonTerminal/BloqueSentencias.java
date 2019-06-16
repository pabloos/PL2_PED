/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.syntax.nonTerminal;

import java.util.ArrayList;
import java.util.List;

import es.uned.lsi.compiler.semantic.type.TypeIF;
import es.uned.lsi.compiler.intermediate.OperandIF;

public class BloqueSentencias extends NonTerminal {

    private Boolean tieneDevuelve;
    private TypeIF tipoDevuelve;
    private List codigoIntermedio;
    private OperandIF resultado;
    public Boolean vacio = false;
    

    public BloqueSentencias() {
        this.tieneDevuelve = false;
        this.codigoIntermedio = new ArrayList();
    }

    public BloqueSentencias(Boolean vacio) {
        this.vacio = vacio;
    }

    public BloqueSentencias(TypeIF tipoDevuelve) {
        super();
        this.tipoDevuelve = tipoDevuelve;
        this.setTieneDevuelve(true);
    }

    public TypeIF getTipoDevuelve() {
        return tipoDevuelve;
    }

    public void setTipoDevuelve(TypeIF tipoDevuelve) {
        this.tipoDevuelve = tipoDevuelve;
        setTieneDevuelve(true);
    }

    public Boolean getTieneDevuelve() {
        return tieneDevuelve;
    }

    public void setTieneDevuelve(Boolean tieneDevuelve) {
        this.tieneDevuelve = tieneDevuelve;
    }

    public OperandIF getResultado() {
        return resultado;
    }

    public void setResultado(OperandIF resultado) {
        this.resultado = resultado;
    }
}