package ch.jalu.wordeval.evaluators;

import lombok.Getter;

/**
 * Evaluation result of a word: contains a score and optionally a key, typically {@code null} or the portion of the
 * word which has the evaluator's criteria (for non-obvious cases, or if multiple results may be produced for a word).
 * In a later step, it may also be possible to sort by the key as the second order to filter out the best results.
 *
 * Note that {@link #skipEntry()} may be returned, in which case the word should not even be stored for the given
 * evaluator (the word does not have the criteria the evaluator is looking for).
 *
 * @param <R> the score type
 */
@Getter
public class EvaluationResult<R extends Comparable<R>> {

  private static final EvaluationResult SKIP_RESULT = new EvaluationResult<>(null, null);

  private final R score;
  private final String key;

  private EvaluationResult(R score, String key) {
    this.score = score;
    this.key = key;
  }

  public static <R extends Comparable<R>> EvaluationResult<R> of(R score, String key) {
    return new EvaluationResult<>(score, key);
  }

  public static <R extends Comparable<R>> EvaluationResult<R> skipEntry() {
    return SKIP_RESULT;
  }

  public boolean isSkipEntry() {
    return this == SKIP_RESULT;
  }
}
