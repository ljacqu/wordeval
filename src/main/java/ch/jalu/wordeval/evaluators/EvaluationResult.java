package ch.jalu.wordeval.evaluators;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Evaluation result of a word: contains a score and optionally a key, typically {@code null} or the portion of the
 * word which has the evaluator's criteria (for non-obvious cases, or if multiple results may be produced for a word).
 * In a later step, it may also be possible to sort by the key as the second order to filter out the best results.
 *
 * A negative score may be returned, in which case the word should not even be stored for the given
 * evaluator (the word does not have the criteria the evaluator is looking for).
 */
@Getter
@AllArgsConstructor
public class EvaluationResult {

  private final double score;
  private final String key;

}
