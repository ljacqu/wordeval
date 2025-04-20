package ch.jalu.wordeval;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.DictionaryProcessor;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.export.ExportService;
import ch.jalu.wordeval.evaluators.processing.EvaluatorInitializer;
import ch.jalu.wordeval.evaluators.processing.EvaluatorProcessor;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.util.TimeLogger;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Entry point of the <i>wordeval</i> application: generates JSON export of the evaluator results.
 */
@Slf4j
@SpringBootApplication
public class WordEvalMain implements CommandLineRunner {

  @Autowired
  private AppData appData;

  /**
   * Entry point method.
   *
   * @param args .
   */
  public static void main(String[] args) {
    SpringApplication.run(WordEvalMain.class, args);
  }

  @Override
  public void run(String... args) {
    List<String> codes = List.of("eu", "en-us", "fr");

    for (String code : codes) {
      exportLanguage(code);
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

    ExportService exportService = new ExportService();
    exportService.export(language, evaluatorProcessor.streamThroughAllEvaluators());
    timeLogger.lap("finished export");
    timeLogger.logWithOverallTime("Finished language '" + code + "'");
  }
}
