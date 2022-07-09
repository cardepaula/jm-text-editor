package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;

import util.TabHash;

public class JMPortugueseDictionary {

	TabHash<String> portugueseWords;
	int tamanhoTabela = 980932;
	private int constante = 980957;

	public JMPortugueseDictionary() {
		this.createTable();
	}

	private void createTable() {
		BufferedReader bufferReader;
		String nomearq = "src/core/pt.dic";
		String linha;
		int pos = 0;

		try {
			File file = new File(nomearq);
			bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-16"));
			System.out.println("Carregando dicionário");
			
			this.portugueseWords = new TabHash<String>(tamanhoTabela, constante);

			int colisoes = 0;
			while ((linha = bufferReader.readLine()) != null) {

				if (!linha.substring(0, 1).matches("%*")) {
					pos = portugueseWords.put(linha, linha);
				}

				if (pos == -1) {
					colisoes++;
				}
			}
//			System.out.println("Colisoes: " + colisoes + " | %: " + ((colisoes * 100) / tamanhoTabela));
			System.out.println("Dicionário carregado");
			bufferReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TabHash<String> getPortugueseWords() {
		return portugueseWords;
	}

	public boolean hasPortugueseWord(String word) {
		LinkedList<String> words = this.portugueseWords.get(word);
		return words.contains(word);
	}

	public LinkedList<String> getSugestions(String word) {
		int hash = this.portugueseWords.getHash(word);
		LinkedList<String> sugestions = this.portugueseWords.get(hash);

		if (sugestions.isEmpty()) {
			int i = 1;

			while ((hash - i) >= 0 || (hash + i) <= this.tamanhoTabela) {

				if ((hash - i) >= 0) {
					LinkedList<String> w1 = this.portugueseWords.get(hash - i);
					if (!w1.isEmpty())
						return w1;
				}

				if ((hash + i) <= this.tamanhoTabela) {
					LinkedList<String> w2 = this.portugueseWords.get(hash + i);
					if (!w2.isEmpty())
						return w2;
				}

				i++;
			}
		}
		return sugestions;
	}
}
