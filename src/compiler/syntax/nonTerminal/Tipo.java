package compiler.syntax.nonTerminal;

import es.uned.lsi.compiler.semantic.type.TypeIF;
import es.uned.lsi.compiler.intermediate.QuadrupleIF;
import es.uned.lsi.compiler.intermediate.TemporalIF;

import java.util.List;

public class Tipo extends NonTerminal {
	private List<QuadrupleIF> code;
	private TypeIF type;
	private TemporalIF temporal;
	private int valor;
	
	public List<QuadrupleIF> getCode() {
		return code;
	}
	public void setCode(List<QuadrupleIF> code) {
		this.code = code;
	}
	public TypeIF getType() {
		return type;
	}
	public void setType(TypeIF type) {
		this.type = type;
	}
	public TemporalIF getTemporal() {
		return temporal;
	}
	public void setTemporal(TemporalIF temporal) {
		this.temporal = temporal;
	}
	public int getValor() {
		return valor;
	}
	public void setValor(int valor) {
		this.valor = valor;
	}
}
