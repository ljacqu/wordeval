package ch.ljacqu.wordeval.language;

import java.util.List;

import ch.ljacqu.wordeval.evaluation.Evaluator;

public class AfDictionary extends Dictionary {
	
	public AfDictionary(List<Evaluator> evaluators) {
		super("dict/af.dic", "af", DictionaryType.WORD_PER_LINE, evaluators);
	}
	
	public AfDictionary(String fileName, DictionaryType type, List<Evaluator> evaluators) {
		super(fileName, "af", type, evaluators);
	}

	@Override
	public String sanitizeWord(String word) {
		int dashPosition = word.indexOf('/');
		if (dashPosition != -1) {
			return word.substring(0, dashPosition);
		}
		return word;
	}
	
}
