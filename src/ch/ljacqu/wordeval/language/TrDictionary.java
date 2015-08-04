package ch.ljacqu.wordeval.language;

import java.util.List;

import ch.ljacqu.wordeval.evaluation.Evaluator;

public class TrDictionary extends Dictionary {
	
	public static final String CODE = "tr";
	
	public TrDictionary(List<Evaluator> evaluators) {
		super("dict/tr.dic", CODE, DictionaryType.WORD_PER_LINE, evaluators);
	}
	
	public TrDictionary(String fileName, DictionaryType type, List<Evaluator> evaluators) {
		super(fileName, CODE, type, evaluators);
	}

	@Override
	public String sanitizeWord(String word) {
		return word.substring(0, word.indexOf(' '));
	}
	
}
