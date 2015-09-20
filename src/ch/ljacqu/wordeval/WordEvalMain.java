package ch.ljacqu.wordeval;

import static ch.ljacqu.wordeval.language.LetterType.CONSONANTS;
import static ch.ljacqu.wordeval.language.LetterType.VOWELS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import ch.ljacqu.wordeval.dictionary.Dictionary;
import ch.ljacqu.wordeval.evaluation.AlphabeticalSequence;
import ch.ljacqu.wordeval.evaluation.AlphabeticalOrder;
import ch.ljacqu.wordeval.evaluation.Anagrams;
import ch.ljacqu.wordeval.evaluation.ConsecutiveLetterPairs;
import ch.ljacqu.wordeval.evaluation.ConsecutiveVowelCount;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.Isograms;
import ch.ljacqu.wordeval.evaluation.LongWords;
import ch.ljacqu.wordeval.evaluation.MonotoneVowel;
import ch.ljacqu.wordeval.evaluation.Palindromes;
import ch.ljacqu.wordeval.evaluation.SameLetterConsecutive;
import ch.ljacqu.wordeval.evaluation.export.ExportService;
import ch.ljacqu.wordeval.language.Language;

/**
 * Entry point of the <i>wordeval</i> application.
 */
public class WordEvalMain {
  private WordEvalMain() {
  }

  /**
   * Entry point method.
   * @param args .
   * @throws IOException If a dictionary could not be read
   */
  public static void main(String[] args) throws IOException {
    //Set<String> codes = Dictionary.getAllCodes();
    String[] codes = { "eu", "fr", "ru" };
    
    for (String code : codes) {
      exportLanguage(code);
    }
  }

  /**
   * Exports the evaluator results for a dictionary into the /export folder.
   * @param code The code of the dictionary to evaluate
   * @throws IOException If the dictionary cannot be read
   */
  public static void exportLanguage(String code) throws IOException {
    System.out.println("Exporting language '" + code + "'");
    Language language = Language.get(code);
    List<Long> times = new ArrayList<Long>();
    times.add(System.nanoTime());

    List<Evaluator<?>> evaluators = new ArrayList<>();
    evaluators.add(new AlphabeticalOrder());
    evaluators.add(new AlphabeticalSequence());
    evaluators.add(new Anagrams());
    evaluators.add(new ConsecutiveLetterPairs());
    evaluators.add(new ConsecutiveVowelCount(VOWELS, language));
    evaluators.add(new ConsecutiveVowelCount(CONSONANTS, language));
    evaluators.add(new Isograms());
    evaluators.add(new LongWords());
    evaluators.add(new MonotoneVowel(VOWELS, language));
    evaluators.add(new MonotoneVowel(CONSONANTS, language));
    evaluators.add(new Palindromes());
    evaluators.add(new SameLetterConsecutive());
    outputDiff(times, "instantiated evaluators");

    Dictionary dictionary = Dictionary.getDictionary(code, evaluators);
    outputDiff(times, "got dictionary object");

    dictionary.process();
    outputDiff(times, "processed dictionary");

    ExportService.exportToFile(evaluators, "export/" + code + ".json");
    outputDiff(times, "finished export");

    times.add(times.get(0)); // ;)
    outputDiff(times, "= total time");
    System.out.println("-------------");
  }

  private static void outputDiff(List<Long> times, String description) {
    double difference = (System.nanoTime() - times.get(times.size() - 1)) / 1000000000.0;
    times.add(System.nanoTime());
    System.out.println(difference + "\t\t" + description);
  }

}
