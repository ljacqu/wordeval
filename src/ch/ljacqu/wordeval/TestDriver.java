package ch.ljacqu.wordeval;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ljacqu.wordeval.evaluation.ConsecutiveLetterPairs;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.LongWords;
import ch.ljacqu.wordeval.evaluation.SameLetterConsecutive;
import ch.ljacqu.wordeval.evaluation.VowelCount;
import ch.ljacqu.wordeval.evaluation.VowelCount.SearchType;
import ch.ljacqu.wordeval.language.AfDictionary;
import ch.ljacqu.wordeval.language.Dictionary;


public class TestDriver {

	public static void main(String[] args) throws IOException {
		List<Evaluator> evaluators = new ArrayList<Evaluator>();
		evaluators.add(new ConsecutiveLetterPairs());
		evaluators.add(new LongWords());
		evaluators.add(new SameLetterConsecutive());
		evaluators.add(new VowelCount(SearchType.VOWELS));
		evaluators.add(new VowelCount(SearchType.CONSONANTS));
		
		
		Dictionary afDictionary = new AfDictionary(evaluators);
		afDictionary.processDictionary();
		
		for (Evaluator evaluator : evaluators) {
			System.out.println("-------\n" + evaluator.getClass().getSimpleName());
			evaluator.outputAggregatedResult();
		}
		
	}
}
