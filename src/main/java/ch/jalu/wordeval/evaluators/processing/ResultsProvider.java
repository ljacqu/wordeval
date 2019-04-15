package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.result.EvaluationResult;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Argument passed to {@link ch.jalu.wordeval.evaluators.PostEvaluator} instances to allow them to fetch
 * a <b>read-only</b> view of other evaluator's results.
 */
public class ResultsProvider {

  private final Map<AllWordsEvaluator, ResultStore> evaluatorResults;

  /**
   * Constructor.
   *
   * @param evaluatorResults the evaluator processor with the results
   */
  ResultsProvider(Map<AllWordsEvaluator, ResultStore> evaluatorResults) {
    this.evaluatorResults = evaluatorResults;
  }

  /** // TODO JAVADOC
   * Returns the results of the evaluator of the given class. Throws an exception if more than one evaluator of
   * this class exists.
   *
   * @param clz the evaluator class to search for
   * @return the results of the evaluator of the provided class
   */
  public <R extends EvaluationResult, E extends AllWordsEvaluator<R>> ImmutableList<R> getResultsOfEvaluatorOfType(
      Class<E> clz) {
    return getResultsOfEvaluatorOfType(clz, e -> true);
  }

  /** TODO JAVADOC
   * Returns the results of the evaluator that is an instance of the given class and which satisfies the provided
   * predicate. An exception is thrown if more than one evaluator is matched successfully.
   *
   * @param clz the evaluator class to search for
   * @param predicate the predicate the evaluator must satisfy
   * @param <E> the evaluator type
   * @return the results of the matching evaluator
   */
  public <R extends EvaluationResult, E extends AllWordsEvaluator<R>> ImmutableList<R> getResultsOfEvaluatorOfType(
      Class<E> clz, Predicate<E> predicate) {

    E evaluator = findEvaluatorOfTypeMatching(clz, predicate);
    return evaluatorResults.get(evaluator).getEntries();
  }

  private <E extends AllWordsEvaluator> E findEvaluatorOfTypeMatching(Class<E> evaluatorClass, Predicate<E> predicate) {
    List<E> matchingEvaluators = evaluatorResults.keySet().stream()
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
