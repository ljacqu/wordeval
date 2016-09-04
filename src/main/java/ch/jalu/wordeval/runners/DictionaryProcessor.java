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
 * Loads a dictionary and passes each word to the evaluators.
 */
public class DictionaryProcessor {

  private final DataUtils dataUtils;

  public DictionaryProcessor(DataUtils dataUtils) { // TODO: Remove DataUtils from constructor
    this.dataUtils = dataUtils;
  }

  public long process(DictionarySettings dictionary, Iterable<Evaluator<?>> evaluators) {
    final EvaluatorInvoker invoker = new EvaluatorInvoker(evaluators);
    final Sanitizer sanitizer = dictionary.getSanitizer();
    final Language language = dictionary.getLanguage();
    final WordFormsBuilder wordFormsBuilder = new WordFormsBuilder(language);

    long totalWords = dataUtils.readFileLines(dictionary.getFile())
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
