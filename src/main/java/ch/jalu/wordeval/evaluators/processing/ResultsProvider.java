package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import com.google.common.collect.ImmutableMultimap;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Argument passed to {@link ch.jalu.wordeval.evaluators.PostEvaluator} instances to allow them to fetch
 * a <b>read-only</b> view of other evaluator's results.
 */
public class ResultsProvider {

  private final Map<WordEvaluator, ResultStore> wordEvaluators;

  /**
   * Constructor.
   *
   * @param wordEvaluators the evaluator processor with the results
   */
  ResultsProvider(Map<WordEvaluator, ResultStore> wordEvaluators) {
    this.wordEvaluators = wordEvaluators;
  }

  /**
   * Returns the results of the evaluator of the given class. Throws an exception if more than one evaluator of
   * this class exists.
   *
   * @param clz the evaluator class to search for
   * @return the results of the evaluator of the provided class
   */
  public ImmutableMultimap<Double, EvaluatedWord> getResultsOfEvaluatorOfType(Class<? extends WordEvaluator> clz) {
    return getResultsOfEvaluatorOfType(clz, e -> true);
  }

  /**
   * Returns the results of the evaluator that is an instance of the given class and which satisfies the provided
   * predicate. An exception is thrown if more than one evaluator is matched successfully.
   *
   * @param clz the evaluator class to search for
   * @param predicate the predicate the evaluator must satisfy
   * @param <W> the evaluator type
   * @return the results of the matching evaluator
   */
  public <W extends WordEvaluator> ImmutableMultimap<Double, EvaluatedWord> getResultsOfEvaluatorOfType(Class<W> clz,
                                                                                               Predicate<W> predicate) {
    W evaluator = findEvaluatorOfTypeMatching(clz, predicate);
    return wordEvaluators.get(evaluator).getEntries();
  }

  private <W extends WordEvaluator> W findEvaluatorOfTypeMatching(Class<W> evaluatorClass,
                                                                  Predicate<W> predicate) {
    List<W> matchingEvaluators = wordEvaluators.keySet().stream()
      .filter(evaluatorClass::isInstance)
      .map(evaluatorClass::cast)
      .filter(predicate)
      .collect(Collectors.toList());
    if (matchingEvaluators.size() == 1) {
      return matchingEvaluators.get(0);
    } else if (matchingEvaluators.isEmpty()) {
      throw new IllegalStateException("Found no matching evaluator");
    } else {
      throw new IllegalStateException("Found " + matchingEvaluators.size() + " evaluators but expected only 1");
    }
  }
}
