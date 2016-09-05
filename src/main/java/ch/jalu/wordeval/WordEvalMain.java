package ch.jalu.wordeval;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.dictionary.DictionarySettings;
import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.evaluation.export.ExportService;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.runners.DictionaryProcessor;
import ch.jalu.wordeval.runners.EvaluatorInitializer;
import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Entry point of the <i>wordeval</i> application: generates JSON export of the evaluator results.
 */
@Log4j2
public final class WordEvalMain {

  private final AppData appData;
  private final DictionaryProcessor dictionaryProcessor;
  
  private WordEvalMain() {
    appData = new AppData();
    dictionaryProcessor = new DictionaryProcessor(new DataUtils());
  }

  /**
   * Entry point method.
   *
   * @param args .
   */
  public static void main(String[] args) {
    // All codes: DictionarySettings.getAllCodes()
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
    log.info("Exporting language '{}'", code);
    List<Long> times = new ArrayList<>();
    times.add(System.nanoTime());

    DictionarySettings dictionary = appData.getDictionary(code);
    Language language = dictionary.getLanguage();
    outputDiff(times, "got dictionary object");

    EvaluatorInitializer initializer = new EvaluatorInitializer(language);
    List<Evaluator<?>> evaluators = initializer.getEvaluators();
    outputDiff(times, "instantiated evaluators");

    long totalWords = dictionaryProcessor.process(dictionary, evaluators);
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
