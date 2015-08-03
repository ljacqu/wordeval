package main;
import java.io.IOException;

import sanitizing.AfDictionaryCleaner;
import sanitizing.Sanitizer;
import evaluation.ConsecutiveLetterPairs;
import evaluation.Evaluation;
import evaluation.LongWords;
import evaluation.SameLetterConsecutive;
import evaluation.VowelCount;
import evaluation.VowelCount.SearchType;


public class TestDriver {

	public static void main(String[] args) throws IOException {
		Evaluation evaluator = new ConsecutiveLetterPairs();
		Sanitizer sanitizer = new AfDictionaryCleaner();
		DictionaryLoader dictionaryLoader = new DictionaryLoader("src/af.dic", evaluator, sanitizer);
		evaluator.outputAggregatedResult();
	}
}
