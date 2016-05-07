package ch.jalu.wordeval;

import ch.jalu.wordeval.evaluation.AllVowels;
import ch.jalu.wordeval.evaluation.AlphabeticalOrder;
import ch.jalu.wordeval.evaluation.Anagrams;
import ch.jalu.wordeval.evaluation.BackwardsPairs;
import ch.jalu.wordeval.evaluation.ConsecutiveLetterPairs;
import ch.jalu.wordeval.evaluation.DiacriticHomonyms;
import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.evaluation.FullPalindromes;
import ch.jalu.wordeval.evaluation.Isograms;
import ch.jalu.wordeval.evaluation.SameLetterConsecutive;
import ch.jalu.wordeval.evaluation.SingleVowel;
import ch.jalu.wordeval.evaluation.export.ExportService;
import ch.jalu.wordeval.language.LetterType;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.evaluation.AlphabeticalSequence;
import ch.jalu.wordeval.evaluation.ConsecutiveVowelCount;
import ch.jalu.wordeval.evaluation.LongWords;
import ch.jalu.wordeval.evaluation.Palindromes;
import ch.jalu.wordeval.evaluation.VowelCount;
import ch.jalu.wordeval.evaluation.WordCollector;
import ch.jalu.wordeval.language.Language;
import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    List<Long> times = new ArrayList<>();
    times.add(System.nanoTime());
    
    Dictionary dictionary = Dictionary.getDictionary(code);
    Language language = dictionary.getLanguage();
    outputDiff(times, "got dictionary object");

    List<Evaluator<?>> evaluators = new ArrayList<>();
    evaluators.add(new AllVowels(LetterType.VOWELS));
    evaluators.add(new AllVowels(LetterType.CONSONANTS));
    evaluators.add(new AlphabeticalOrder());
    evaluators.add(new AlphabeticalSequence());
    evaluators.add(new Anagrams());
    evaluators.add(new BackwardsPairs());
    evaluators.add(new ConsecutiveLetterPairs());
    evaluators.add(new ConsecutiveVowelCount(LetterType.VOWELS, language));
    evaluators.add(new ConsecutiveVowelCount(LetterType.CONSONANTS, language));
    evaluators.add(new DiacriticHomonyms(language.getLocale()));
    evaluators.add(new FullPalindromes());
    evaluators.add(new Isograms());
    evaluators.add(new LongWords());
    evaluators.add(new Palindromes());
    evaluators.add(new SameLetterConsecutive());
    evaluators.add(new SingleVowel(LetterType.VOWELS));
    evaluators.add(new SingleVowel(LetterType.CONSONANTS));
    evaluators.add(new VowelCount(LetterType.VOWELS, language));
    evaluators.add(new VowelCount(LetterType.CONSONANTS, language));
    evaluators.add(new WordCollector());
    outputDiff(times, "instantiated evaluators");

    long totalWords = dictionary.process(evaluators);
    Map<String, String> metaInfo = ImmutableMap.of(
        "dictionary", code,
        "language", language.getName(),
        "gen_date", String.valueOf(System.currentTimeMillis() / 1000L),
        "words", String.valueOf(totalWords));
    outputDiff(times, "processed dictionary");

    ExportService.exportToFile(evaluators, "export/" + code + ".json", metaInfo);
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
