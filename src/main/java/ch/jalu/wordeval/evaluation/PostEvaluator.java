package ch.jalu.wordeval.evaluation;

/**
 * Common interface for an evaluator which generates results based on another evaluator.
 *
 * @param <T> the base class the post evaluator will generate results from
 */
public interface PostEvaluator<T extends Evaluator> {

  /**
   * Runs the post evaluator with the given base.
   *
   * @param evaluator the base for the post evaluator to generate results from
   * @see #castAndEvaluate
   */
  void evaluateWith(T evaluator);

  /**
   * Returns the required type of the base evaluator.
   *
   * @return The class of the base evaluator
   */
  Class<T> getType();

  /**
   * Returns whether or not the given evaluator can be used as base for the post evaluator. This can be used to
   * define more granular matching, e.g. when two evaluators of the same type exist for different letter types.
   *
   * @param evaluator the evaluator to check
   * @return true if the evaluator can be used as base, false otherwise
   * @see #isBaseMatch
   */
  default boolean isMatch(T evaluator) {
    return true;
  }

  /**
   * Convenience method for checking whether an evaluator matches the required base type {@code <T>} and
   * the specific method {@link #isMatch}.
   *
   * @param evaluator the evaluator to check
   * @return true if the evaluator can be used as base, false otherwise
   */
  default boolean isBaseMatch(Evaluator<?> evaluator) {
    return getType().isAssignableFrom(evaluator.getClass())
        && isMatch(getType().cast(evaluator));
  }

  /**
   * Convenience method for passing the base class to the post evaluator without having to cast to the
   * required base type {@code <T>}.
   *
   * @param evaluator the base evaluator
   */
  default void castAndEvaluate(Evaluator<?> evaluator) {
    evaluateWith(getType().cast(evaluator));
  }


}
