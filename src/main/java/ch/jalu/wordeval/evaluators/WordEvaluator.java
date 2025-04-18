package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.result.EvaluationResult;

import java.util.Collection;

/**
 * Simple evaluator which produces an {@link EvaluationResult} object for each {@link Word} it is passed.
 */
public interface WordEvaluator<R extends EvaluationResult> extends AllWordsEvaluator<R> {

  @Override
  default void evaluate(Collection<Word> words) {
    words.forEach(this::evaluate);
  }

  /**
   * Evaluates the given word object.
   *
   * @param word the word to process
   */
  void evaluate(Word word);

}
