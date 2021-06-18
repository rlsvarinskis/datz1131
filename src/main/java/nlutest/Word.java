package nlutest;

import java.util.ArrayList;
import java.util.HashMap;

public class Word {
	//Original word
	public String word;
	//Lemma
	public String singular;
	//The full dependency to the parent word
	public String relToPar;
	//Map of simplified dependencies (obl:arg turns into obl) to all words below this word with that dependency
	public HashMap<String, ArrayList<Word>> words;
	
	public Word(String word) {
		this.word = word;
		this.words = new HashMap<String, ArrayList<Word>>();
	}
}
