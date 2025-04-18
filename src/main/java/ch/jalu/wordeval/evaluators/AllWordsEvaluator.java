package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.EvaluationResult;

import java.util.Collection;

public non-sealed interface AllWordsEvaluator<R extends EvaluationResult> extends Evaluator<R> {

  /**
   * Evaluates all words and saves the relevant results to the provided result store.
   *
   * @param words the words to process
   * @param resultStore the result store to add to
   */
  void evaluate(Collection<Word> words, ResultStore<R> resultStore);
}
