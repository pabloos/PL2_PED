package compiler.syntax.nonTerminal;

import java.util.List;

import es.uned.lsi.compiler.intermediate.QuadrupleIF;
import es.uned.lsi.compiler.intermediate.TemporalIF;
import es.uned.lsi.compiler.semantic.type.TypeIF;

public class Var extends NonTerminal {
    private TypeIF type;
	private String value;
	private List<QuadrupleIF> code;
	private TemporalIF temporal;
	
	private String name;

	private int line;
	private int column;

	public int getLine() {
		return this.line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return this.column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

    public Var() {
		
	}
	
	public Var(TypeIF type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public Var(Var constante) {
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
