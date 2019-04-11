package compiler.syntax.nonTerminal;

import es.uned.lsi.compiler.semantic.type.TypeIF;

public class CampoRegistro extends NonTerminal{
    private TypeIF tipo;
    private String name;

	public CampoRegistro(String name, TypeIF tipo) {
		this.tipo = tipo;
		this.name = name;
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
    }
    
    public TypeIF getTipo() {
		return tipo;
	}

	public void setTipo(TypeIF tipo) {
		this.tipo = tipo;
	}
}