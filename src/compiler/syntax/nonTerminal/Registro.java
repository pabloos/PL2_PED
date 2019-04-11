package compiler.syntax.nonTerminal;

import java.util.HashMap;

public class Registro extends NonTerminal {
    private String name;
    private HashMap<String, CampoRegistro> campos;

    public Registro(String name) {
    	this.name = name;
    	this.campos = new HashMap<String, CampoRegistro>();
    }
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
    }

	public HashMap<String, CampoRegistro> getCampos() {
		return campos;
	}

	public void setCampos(HashMap<String, CampoRegistro> campos) {
		this.campos = campos;
	}
}