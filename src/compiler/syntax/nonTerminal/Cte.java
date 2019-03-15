package compiler.syntax.nonTerminal;

import es.uned.lsi.compiler.semantic.type.TypeIF;

import java.util.List;

import es.uned.lsi.compiler.intermediate.QuadrupleIF;
import es.uned.lsi.compiler.intermediate.TemporalIF;

public class Cte extends NonTerminal {
	private TypeIF type;
	private String value;
	private List<QuadrupleIF> code;
	private TemporalIF temporal;
	
	public Cte() {
		
	}
	
	public Cte(TypeIF type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public Cte(Cte constante) {
		this.type = constante.getType();
		this.code = constante.getCode();
		this.temporal = constante.getTemporal();
	}

	public TypeIF getType() {
		return type;
	}

	public void setType(TypeIF type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<QuadrupleIF> getCode() {
		return code;
	}

	public void setCode(List<QuadrupleIF> code) {
		this.code = code;
	}

	public TemporalIF getTemporal() {
		return temporal;
	}

	public void setTemporal(TemporalIF temporal) {
		this.temporal = temporal;
	}
}
