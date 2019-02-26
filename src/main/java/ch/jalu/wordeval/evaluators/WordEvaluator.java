package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;

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
  EvaluationResult evaluate(Word word);

}
