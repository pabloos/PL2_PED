package compiler.syntax.nonTerminal;

import es.uned.lsi.compiler.semantic.type.TypeIF;

import java.util.List;

import es.uned.lsi.compiler.intermediate.QuadrupleIF;
import es.uned.lsi.compiler.intermediate.TemporalIF;

public class Cte extends NonTerminal {
	private String name;

	private String line;
	private String column;

	private TypeIF type;
	private int value;
	private List<QuadrupleIF> code;
	private TemporalIF temporal;

	public String getLine() {
		return this.line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getColumn() {
		return this.column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public Cte() {
		
	}
	
	public Cte(String name, TypeIF type, int value, String line, String column) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.line = line;
		this.column = column;
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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
    public String toString() {
        return "Name:"+this.getName() + "; Value:" + this.getValue();
    }
}
