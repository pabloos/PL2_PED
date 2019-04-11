package compiler.syntax.nonTerminal;

import java.util.ArrayList;
import java.util.List;

public class Lista extends NonTerminal {
	private List lista = null;

	public Lista() {
		this.lista = new ArrayList();
	}
	
    public Lista(Lista lista) {
        this.lista = new ArrayList();
        
        for (int i = 0; i < lista.size(); i++) {
        	lista.add(lista.get(i));
        }
    }

	public boolean add(Object objeto) {
        return lista.add(objeto);
    }

    public int size() {
        return lista.size();
    }

    public Object get(int indice) {
        return lista.get(indice);
    }
}
