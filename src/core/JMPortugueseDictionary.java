package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import util.TabHash;

public class JMPortugueseDictionary {

	TabHash<String> portugueseWords;
	
	public JMPortugueseDictionary() {
		this.createTable();
	}
	
	private void createTable() {
		int tamanhoTabela = 980932;
		BufferedReader bufferReader;
		String nomearq = "src/core/pt.dic";
		String linha;
		int pos = 0;


		try {
			File file = new File(nomearq);
			bufferReader = new BufferedReader( new InputStreamReader(new FileInputStream(file), "UTF-16"));

			this.portugueseWords = new TabHash<String>(tamanhoTabela);

			int colisoes = 0;
			while ((linha = bufferReader.readLine()) != null) {
				
				if (!linha.substring(0, 1).matches("%*")) {
					pos = portugueseWords.put(linha, linha);					
				}
				

				if (pos == -1) {
					colisoes++;
					System.out.println("Palavra: " + linha + " | Pos: " + pos);
				}
			}
			System.out.println("Colisoes: " + colisoes + " | %: " + ((colisoes * 100) / tamanhoTabela));
			bufferReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public TabHash<String> getPortugueseWords() {
		return portugueseWords;
	}
}
