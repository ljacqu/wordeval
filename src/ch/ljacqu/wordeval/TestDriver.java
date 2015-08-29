package ch.ljacqu.wordeval;

import java.util.ArrayList;
import java.util.List;
import ch.ljacqu.wordeval.evaluation.AlphabeticalOrder;
import ch.ljacqu.wordeval.evaluation.AlphabeticSequence;
import ch.ljacqu.wordeval.evaluation.ConsecutiveLetterPairs;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.Isograms;
import ch.ljacqu.wordeval.evaluation.LongWords;
import ch.ljacqu.wordeval.evaluation.MonotoneVowel;
import ch.ljacqu.wordeval.evaluation.Palindromes;
import ch.ljacqu.wordeval.evaluation.SameLetterConsecutive;
import ch.ljacqu.wordeval.evaluation.ConsecutiveVowelCount;
import ch.ljacqu.wordeval.language.Dictionary;

public class TestDriver {

  public static void main(String[] args) throws Exception {
    List<Evaluator> evaluators = new ArrayList<Evaluator>();
    evaluators.add(new ConsecutiveLetterPairs());
    evaluators.add(new LongWords());
    evaluators.add(new SameLetterConsecutive());
    evaluators.add(new ConsecutiveVowelCount(LetterType.VOWELS));
    evaluators.add(new ConsecutiveVowelCount(LetterType.CONSONANTS));
    evaluators.add(new AlphabeticalOrder());
    evaluators.add(new AlphabeticSequence());
    evaluators.add(new MonotoneVowel(LetterType.VOWELS));
    evaluators.add(new MonotoneVowel(LetterType.CONSONANTS));
    evaluators.add(new Isograms());
    evaluators.add(new Palindromes());

    Dictionary dictionary = Dictionary.getLanguageDictionary("af", evaluators);
    dictionary.processDictionary();

    for (Evaluator evaluator : evaluators) {
      //System.out.println("-------\n" + evaluator.getClass().getSimpleName());
      //evaluator.outputAggregatedResult();
      System.out.println(evaluator.toExportObject());
    }

  }
}
