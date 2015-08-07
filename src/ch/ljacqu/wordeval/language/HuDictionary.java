package ch.ljacqu.wordeval.language;

import java.util.List;

import ch.ljacqu.wordeval.evaluation.Evaluator;

public class HuDictionary extends Dictionary {
	
	public static final String CODE = "tr";
	
	public HuDictionary(List<Evaluator> evaluators) {
		super("dict/hu.dic", CODE, DictionaryType.WORD_PER_LINE, evaluators);
	}
	
	public HuDictionary(String fileName, DictionaryType type, List<Evaluator> evaluators) {
		super(fileName, CODE, type, evaluators);
	}

	@Override
	public String sanitizeWord(String word) {
		int index = Math.min(getIndexOrLast(word, '/'), getIndexOrLast(word, '\t'));
		//System.out.println("Got " + index + " for " + word);
		return word.substring(0, index);
	}
	
	private int getIndexOrLast(String word, char chr) {
		return word.indexOf(chr) > -1 ? word.indexOf(chr) : word.length();
	}
	
}
