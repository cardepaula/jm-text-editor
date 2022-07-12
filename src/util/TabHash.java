package util;

import java.util.LinkedList;

public class TabHash<T> {

	private int tamanho, constante = 31;

	public String chave;
	public LinkedList<T>[] tabela;

	public TabHash(int tamanhoTabela) {
		this.tamanho = tamanhoTabela;
		this.createTable();
	}
	
	public TabHash(int tamanhoTabela, int constante) {
		this.tamanho = tamanhoTabela;
		this.constante = constante;
		this.createTable();
	}

	public LinkedList<T> get(String chave) {
		int posHash = this.getHash(chave);

		return this.tabela[posHash];
	}

	public LinkedList<T> get(int posHash) {
		return this.tabela[posHash];
	}
	public int put(T item, String chave) {
		int posHash = this.getHash(chave);
		int colisao = posHash;

		if (!this.tabela[posHash].isEmpty()) {
			colisao = -1;
		}

		this.tabela[posHash].add(item);
		return colisao;
	}

	public void delete(String chave) {
		int posHash = this.getHash(chave);
		this.tabela[posHash].remove(chave);
	}

	public int getHash(String item) {
		int pos = item.charAt(0);
		for (int i = 1; i < item.length(); i++) {
			pos = pos * this.constante + item.charAt(i) ;			
		}
		if (pos < 0) {
			pos = pos * -1;
		}
		return pos % this.tamanho;
	}

	@SuppressWarnings("unchecked")
	private void createTable() {
		this.tabela = new LinkedList[this.tamanho];

		for (int i = 0; i < tabela.length; i++) {
			this.tabela[i] = new LinkedList<T>();
		}
	}
}
