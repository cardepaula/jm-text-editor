package util;

import java.util.LinkedList;

public class TabHash<T> {

	private int tamanho;

	public String chave;
	public LinkedList<T>[] tabela;

	public TabHash(int tamanhoTabela) {
		this.tamanho = tamanhoTabela;
		this.createTable();
	}

	public LinkedList<T> get(String chave) {
		int posHash = this.geraPosicaoHash(chave);

		if(this.tabela[posHash].isEmpty()) {
			return null;			
		}
		return this.tabela[posHash];
	}

	public int put(T item, String chave) {
		int posHash = this.geraPosicaoHash(chave);
		int colisao = posHash;

		if (!this.tabela[posHash].isEmpty()) {
			colisao = -1;
		}

		this.tabela[posHash].add(item);
		return colisao;
	}

	public void delete(String chave) {
		int posHash = this.geraPosicaoHash(chave);
		this.tabela[posHash].remove(chave);
	}

	private int geraPosicaoHash(String item) {
		int pos = item.charAt(0);
		for (int i = 1; i < item.length(); i++) {
			pos = (pos * 33 + item.charAt(i)) % this.tamanho;			
		}
		return pos;
	}

	private void createTable() {
		this.tabela = new LinkedList[this.tamanho];

		for (int i = 0; i < tabela.length; i++) {
			this.tabela[i] = new LinkedList<T>();
		}
	}
}
