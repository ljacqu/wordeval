package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Allows to retrieve another evaluator for a {@link ch.jalu.wordeval.evaluators.PostEvaluator post evaluator}.
 */
@RequiredArgsConstructor
public class AllWordsEvaluatorProvider {

  private final Collection<AllWordsEvaluator> allWordsEvaluator;

  /**
   * Returns the evaluator of the specified class. Throws an exception if no evaluator of the given type exists,
   * or if multiple evaluators exist.
   *
   * @param clz the evaluator class to retrieve
   * @param <E> the evaluator type
   * @return evaluator matching the type
   */
  public <E extends AllWordsEvaluator> E getEvaluator(Class<E> clz) {
    return findEvaluatorOfTypeMatching(clz, e -> true);
  }

  /**
   * Returns the evaluator of the specified class which fulfills the given predicate. Throws an exception if zero
   * or multiple evaluators were matched.
   *
   * @param clz the evaluator class to retrieve
   * @param predicate predicate the evaluator must fulfill in order to be a match
   * @param <E> the evaluator type
   * @return matching evaluator
   */
  public <E extends AllWordsEvaluator> E getEvaluator(Class<E> clz, Predicate<E> predicate) {
    return findEvaluatorOfTypeMatching(clz, predicate);
  }

  private <E extends AllWordsEvaluator> E findEvaluatorOfTypeMatching(Class<E> evaluatorClass,
                                                                      Predicate<E> predicate) {
    List<E> matchingEvaluators = allWordsEvaluator.stream()
        .filter(evaluatorClass::isInstance)
        .map(evaluatorClass::cast)
        .filter(predicate)
        .toList();
    if (matchingEvaluators.size() == 1) {
      return matchingEvaluators.getFirst();
    } else if (matchingEvaluators.isEmpty()) {
      throw new IllegalStateException("Found no matching evaluator of type " + evaluatorClass.getSimpleName());
    } else {
      throw new IllegalStateException("Found " + matchingEvaluators.size() + " evaluators of type "
          + evaluatorClass.getSimpleName() + " but expected only 1");
    }
  }
}
