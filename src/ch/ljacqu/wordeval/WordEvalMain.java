package ch.ljacqu.wordeval;

import static ch.ljacqu.wordeval.language.LetterType.CONSONANTS;
import static ch.ljacqu.wordeval.language.LetterType.VOWELS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ljacqu.wordeval.dictionary.Dictionary;
import ch.ljacqu.wordeval.evaluation.AllVowels;
import ch.ljacqu.wordeval.evaluation.AlphabeticalOrder;
import ch.ljacqu.wordeval.evaluation.AlphabeticalSequence;
import ch.ljacqu.wordeval.evaluation.Anagrams;
import ch.ljacqu.wordeval.evaluation.BackwardsPairs;
import ch.ljacqu.wordeval.evaluation.ConsecutiveLetterPairs;
import ch.ljacqu.wordeval.evaluation.ConsecutiveVowelCount;
import ch.ljacqu.wordeval.evaluation.DiacriticHomonyms;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.FullPalindromes;
import ch.ljacqu.wordeval.evaluation.Isograms;
import ch.ljacqu.wordeval.evaluation.LongWords;
import ch.ljacqu.wordeval.evaluation.Palindromes;
import ch.ljacqu.wordeval.evaluation.SameLetterConsecutive;
import ch.ljacqu.wordeval.evaluation.SingleVowel;
import ch.ljacqu.wordeval.evaluation.VowelCount;
import ch.ljacqu.wordeval.evaluation.WordCollector;
import ch.ljacqu.wordeval.evaluation.export.ExportService;
import ch.ljacqu.wordeval.language.Language;
import lombok.extern.log4j.Log4j2;

/**
 * Entry point of the <i>wordeval</i> application.
 */
@Log4j2
public final class WordEvalMain {

  static {
    AppData.init();
  }
  
  private WordEvalMain() {
  }

  /**
   * Entry point method.
   * @param args .
   */
  public static void main(String[] args) {
    // All codes: DictionarySettings.getAllCodes()
    Iterable<String> codes = Arrays.asList("en-us", "fr");

    for (String code : codes) {
      exportLanguage(code);
    }
  }

  /**
   * Exports the evaluator results for a dictionary into the /export folder.
   * @param code The code of the dictionary to evaluate
   */
  public static void exportLanguage(String code) {
    log.info("Exporting language '{}'", code);
    List<Long> times = new ArrayList<Long>();
    times.add(System.nanoTime());
    
    Dictionary dictionary = Dictionary.getDictionary(code);
    Language language = dictionary.getLanguage();
    outputDiff(times, "got dictionary object");

    List<Evaluator<?>> evaluators = new ArrayList<>();
    evaluators.add(new AllVowels(VOWELS));
    evaluators.add(new AllVowels(CONSONANTS));
    evaluators.add(new AlphabeticalOrder());
    evaluators.add(new AlphabeticalSequence());
    evaluators.add(new Anagrams());
    evaluators.add(new BackwardsPairs());
    evaluators.add(new ConsecutiveLetterPairs());
    evaluators.add(new ConsecutiveVowelCount(VOWELS, language));
    evaluators.add(new ConsecutiveVowelCount(CONSONANTS, language));
    evaluators.add(new DiacriticHomonyms(language.getLocale()));
    evaluators.add(new FullPalindromes());
    evaluators.add(new Isograms());
    evaluators.add(new LongWords());
    evaluators.add(new Palindromes());
    evaluators.add(new SameLetterConsecutive());
    evaluators.add(new SingleVowel(VOWELS));
    evaluators.add(new SingleVowel(CONSONANTS));
    evaluators.add(new VowelCount(VOWELS, language));
    evaluators.add(new VowelCount(CONSONANTS, language));
    evaluators.add(new WordCollector());
    outputDiff(times, "instantiated evaluators");

    dictionary.process(evaluators);
    outputDiff(times, "processed dictionary");

    ExportService.exportToFile(evaluators, "export/" + code + ".json");
    outputDiff(times, "finished export");

    times.add(times.get(0)); // ;)
    outputDiff(times, "= total time");
    log.info("Language finished");
  }

  private static void outputDiff(List<Long> times, String description) {
    double difference = (System.nanoTime() - times.get(times.size() - 1)) / 1000000000.0;
    times.add(System.nanoTime());
    log.info("{}\t\t{}", difference, description);
  }

}
