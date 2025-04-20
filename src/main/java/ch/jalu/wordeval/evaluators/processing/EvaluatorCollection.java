package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.Evaluator;
import ch.jalu.wordeval.evaluators.PostEvaluator;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Set of evaluators that have the same lifecycle (i.e. same language, etc.).
 *
 * @param allWordsEvaluators all word evaluators
 * @param postEvaluators all post evaluators
 */
public record EvaluatorCollection(List<AllWordsEvaluator> allWordsEvaluators, List<PostEvaluator> postEvaluators) {

  /**
   * Convenience constructor for tests to create a collection with a single word evaluator.
   *
   * @param evaluator the word evaluator
   * @return new collection with the word evaluator as its only element
   */
  public static EvaluatorCollection forSingleWordsEvaluator(AllWordsEvaluator evaluator) {
    return new EvaluatorCollection(List.of(evaluator), List.of());
  }

  /**
   * @return number of evaluators this collection has
   */
  public int size() {
    return allWordsEvaluators.size() + postEvaluators.size();
  }

  /**
   * @return stream of all evaluators in this collection
   */
  public Stream<Evaluator> streamThroughAllEvaluators() {
    return Stream.concat(allWordsEvaluators.stream(), postEvaluators.stream());
  }

  /**
   * Returns the evaluator of the specified class. Throws an exception if no evaluator of the given type exists,
   * or if multiple evaluators exist.
   *
   * @param clz the evaluator class to retrieve
   * @param <E> the evaluator type
   * @return evaluator matching the type
   */
  public <E extends AllWordsEvaluator> E getWordEvaluatorOrThrow(Class<E> clz) {
    return getWordEvaluatorOrThrow(clz, e -> true);
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
  public <E extends AllWordsEvaluator> E getWordEvaluatorOrThrow(Class<E> clz, Predicate<E> predicate) {
    List<E> matchingEvaluators = allWordsEvaluators.stream()
        .filter(clz::isInstance)
        .map(clz::cast)
        .filter(predicate)
        .toList();
    if (matchingEvaluators.size() == 1) {
      return matchingEvaluators.getFirst();
    } else if (matchingEvaluators.isEmpty()) {
      throw new IllegalStateException("Found no matching evaluator of type " + clz.getSimpleName());
    } else {
      throw new IllegalStateException("Found " + matchingEvaluators.size() + " evaluators of type "
          + clz.getSimpleName() + " but expected only 1");
    }
  }
}
