package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.processing.ResultStore;

/**
 * Simple evaluator which produces an {@link EvaluationResult} object for each {@link Word} it is passed.
 */
public interface WordEvaluator {

  /**
   * Evaluates the given word object and returns a result.
   *
   * @param word the word to process
   * @return the result for the given word
   */
  void evaluate(Word word, ResultStore resultStore);

}
