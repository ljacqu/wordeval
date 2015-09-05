package ch.ljacqu.wordeval;

import static ch.ljacqu.wordeval.LetterType.CONSONANTS;
import static ch.ljacqu.wordeval.LetterType.VOWELS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ch.ljacqu.wordeval.evaluation.AlphabeticalSequence;
import ch.ljacqu.wordeval.evaluation.AlphabeticalOrder;
import ch.ljacqu.wordeval.evaluation.ConsecutiveLetterPairs;
import ch.ljacqu.wordeval.evaluation.ConsecutiveVowelCount;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.Isograms;
import ch.ljacqu.wordeval.evaluation.LongWords;
import ch.ljacqu.wordeval.evaluation.MonotoneVowel;
import ch.ljacqu.wordeval.evaluation.Palindromes;
import ch.ljacqu.wordeval.evaluation.SameLetterConsecutive;
import ch.ljacqu.wordeval.evaluation.export.ResultsExporter;
import ch.ljacqu.wordeval.language.Dictionary;

public class TestDriver {

  private static final String LANGUAGE = "af";

  public static void main(String[] args) throws IOException {
    List<Evaluator> evaluators = new ArrayList<Evaluator>();
    evaluators.add(new ConsecutiveLetterPairs());
    evaluators.add(new LongWords());
    evaluators.add(new SameLetterConsecutive());
    evaluators.add(new ConsecutiveVowelCount(VOWELS));
    evaluators.add(new ConsecutiveVowelCount(CONSONANTS));
    evaluators.add(new AlphabeticalOrder());
    evaluators.add(new AlphabeticalSequence());
    evaluators.add(new MonotoneVowel(VOWELS));
    evaluators.add(new MonotoneVowel(CONSONANTS));
    evaluators.add(new Isograms());
    evaluators.add(new Palindromes());

    Dictionary dictionary = Dictionary.getLanguageDictionary(LANGUAGE,
        evaluators);
    dictionary.processDictionary();

    ResultsExporter.exportToFile(evaluators, "export/" + LANGUAGE + ".json");

  }
}
