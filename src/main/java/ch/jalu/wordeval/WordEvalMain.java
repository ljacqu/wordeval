package ch.jalu.wordeval;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.processing.EvaluatorInitializer;
import ch.jalu.wordeval.evaluators.processing.EvaluatorProcessor;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.runners.DictionaryProcessor;
import ch.jalu.wordeval.util.TimeLogger;
import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Entry point of the <i>wordeval</i> application: generates JSON export of the evaluator results.
 */
@Log4j2
public final class WordEvalMain {

  private final AppData appData;

  private WordEvalMain() {
    appData = new AppData();
  }

  /**
   * Entry point method.
   *
   * @param args .
   */
  public static void main(String[] args) {
    // All codes: Dictionary.getAllCodes()
    Iterable<String> codes = Arrays.asList("af", "en-us", "fr");

    WordEvalMain main = new WordEvalMain();
    for (String code : codes) {
      main.exportLanguage(code);
    }
  }

  /**
   * Exports the evaluator results for a dictionary into the /export folder.
   *
   * @param code the code of the dictionary to evaluate
   */
  public void exportLanguage(String code) {
    log.info("");
    log.info("Exporting language '{}'", code);
    TimeLogger timeLogger = new TimeLogger(log);

    Dictionary dictionary = appData.getDictionary(code);
    Language language = dictionary.getLanguage();
    timeLogger.lap("Looked up dictionary");

    EvaluatorInitializer initializer = new EvaluatorInitializer(language);
    timeLogger.lap("Instantiated evaluators. Total: " + initializer.getEvaluatorsCount());

    Collection<Word> allWords = DictionaryProcessor.readAllWords(dictionary);
    timeLogger.lap("Loaded all words: total " + allWords.size() + " words");

    EvaluatorProcessor evaluatorProcessor = new EvaluatorProcessor(initializer);
    evaluatorProcessor.processAllWords(allWords);
    timeLogger.lap("Ran all words through all evaluators");

    Map<String, String> metaInfo = ImmutableMap.of(
        "dictionary", code,
        "language", language.getName(),
        "gen_date", String.valueOf(System.currentTimeMillis() / 1000L),
        "words", String.valueOf(allWords.size()));
    timeLogger.lap("processed dictionary");

    // TODO ExportService.exportToFile(evaluators, "export/" + code + ".json", metaInfo);
    timeLogger.lap("finished export");
    timeLogger.logWithOverallTime("Finished language '" + code + "'");
  }
}
