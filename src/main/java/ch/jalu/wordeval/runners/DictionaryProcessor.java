package ch.jalu.wordeval.runners;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.dictionary.DictionarySettings;
import ch.jalu.wordeval.dictionary.Sanitizer;
import ch.jalu.wordeval.dictionary.WordFormsBuilder;
import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.evaluation.EvaluatorInvoker;
import ch.jalu.wordeval.language.Language;
import org.apache.commons.lang3.StringUtils;

/**
 * Processes a dictionary.
 */
public final class DictionaryProcessor {

  private DictionaryProcessor() {
  }

  /**
   * Reads the given dictionary and passes each word to the given collection of evaluators.
   *
   * @param dictionary the dictionary to read
   * @param evaluators the evaluators to use
   * @return number of words processed
   */
  public static long process(DictionarySettings dictionary, Iterable<Evaluator<?>> evaluators) {
    final EvaluatorInvoker invoker = new EvaluatorInvoker(evaluators);
    final Sanitizer sanitizer = dictionary.getSanitizer();
    final Language language = dictionary.getLanguage();
    final WordFormsBuilder wordFormsBuilder = new WordFormsBuilder(language);

    long totalWords = DataUtils.readAllLines(dictionary.getFile())
        .stream()
        .map(sanitizer::isolateWord)
        .filter(StringUtils::isNotEmpty)
        .map(wordFormsBuilder::computeForms)
        .peek(invoker::processWord)
        .count();

    invoker.executePostEvaluators();
    evaluators.forEach(e -> e.filterDuplicateWords(language.getLocale()));
    return totalWords;
  }
}
