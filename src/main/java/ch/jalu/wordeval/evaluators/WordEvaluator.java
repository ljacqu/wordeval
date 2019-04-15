package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.EvaluationResult;

import java.util.Collection;

/**
 * Simple evaluator which produces an {@link EvaluationResult} object for each {@link Word} it is passed.
 */
public interface WordEvaluator<R extends EvaluationResult> extends AllWordsEvaluator<R> {

  @Override
  default void evaluate(Collection<Word> words, ResultStore<R> resultStore) {
    words.forEach(word -> evaluate(word, resultStore));
  }

  /**
   * Evaluates the given word object and returns a result.
   *
   * @param word the word to process
   * @param resultStore the result store to add results to
   */
  void evaluate(Word word, ResultStore<R> resultStore);

}
