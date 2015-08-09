package ch.ljacqu.wordeval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ljacqu.wordeval.evaluation.AlphabeticalOrder;
import ch.ljacqu.wordeval.evaluation.AlphabeticSequence;
import ch.ljacqu.wordeval.evaluation.ConsecutiveLetterPairs;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.LongWords;
import ch.ljacqu.wordeval.evaluation.SameLetterConsecutive;
import ch.ljacqu.wordeval.evaluation.VowelCount;
import ch.ljacqu.wordeval.evaluation.VowelCount.SearchType;
import ch.ljacqu.wordeval.language.AfDictionary;
import ch.ljacqu.wordeval.language.Dictionary;
import ch.ljacqu.wordeval.language.HuDictionary;
import ch.ljacqu.wordeval.language.TrDictionary;

public class TestDriver {

  public static void main(String[] args) throws IOException {
    List<Evaluator> evaluators = new ArrayList<Evaluator>();
    evaluators.add(new ConsecutiveLetterPairs());
    evaluators.add(new LongWords());
    evaluators.add(new SameLetterConsecutive());
    evaluators.add(new VowelCount(SearchType.VOWELS));
    evaluators.add(new VowelCount(SearchType.CONSONANTS));
    evaluators.add(new AlphabeticalOrder());
    evaluators.add(new AlphabeticSequence());

    Dictionary dictionary = new HuDictionary(evaluators);
    dictionary.processDictionary();

    for (Evaluator evaluator : evaluators) {
      System.out.println("-------\n" + evaluator.getClass().getSimpleName());
      evaluator.outputAggregatedResult();
    }

  }
}
