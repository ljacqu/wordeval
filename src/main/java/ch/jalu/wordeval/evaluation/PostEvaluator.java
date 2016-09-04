package ch.jalu.wordeval.evaluation;

/**
 * Common interface for evaluators which generate results based on another evaluator.
 *
 * @param <K> the key type
 * @param <B> the evaluator type the post evaluator is based on
 */
public abstract class PostEvaluator<K extends Comparable, B extends Evaluator> extends Evaluator<K> {

  /**
   * Runs the post evaluator with the given base.
   *
   * @param evaluator the base for the post evaluator to generate results from
   * @see #castAndEvaluate
   */
  public abstract void evaluateWith(B evaluator);

  /**
   * Returns the required type of the base evaluator.
   *
   * @return The class of the base evaluator
   */
  public abstract Class<B> getType();

  /**
   * Returns whether or not the given evaluator can be used as base for the post evaluator. This can be used to
   * define more granular matching, e.g. when two evaluators of the same type exist for different letter types.
   *
   * @param evaluator the evaluator to check
   * @return true if the evaluator can be used as base, false otherwise
   * @see #isBaseMatch
   */
  public boolean isMatch(B evaluator) {
    return true;
  }

  /**
   * Convenience method for checking whether an evaluator matches the required base type {@code <B>} and
   * the specific method {@link #isMatch}.
   *
   * @param evaluator the evaluator to check
   * @return true if the evaluator can be used as base, false otherwise
   */
  public boolean isBaseMatch(Evaluator<?> evaluator) {
    return getType().isAssignableFrom(evaluator.getClass())
        && isMatch(getType().cast(evaluator));
  }

  /**
   * Convenience method for passing the base class to the post evaluator without having to cast to the
   * required base type {@code <B>}.
   *
   * @param evaluator the base evaluator
   */
  public void castAndEvaluate(Evaluator<?> evaluator) {
    evaluateWith(getType().cast(evaluator));
  }

}
