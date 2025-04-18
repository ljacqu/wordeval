package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.result.EvaluationResult;

import java.util.Collection;

public non-sealed interface AllWordsEvaluator<R extends EvaluationResult> extends Evaluator<R> {

  /**
   * Evaluates all words and saves the relevant results.
   *
   * @param words the words to process
   */
  void evaluate(Collection<Word> words);
}
