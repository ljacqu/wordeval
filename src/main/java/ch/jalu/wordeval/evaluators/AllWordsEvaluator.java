package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.processing.ResultStore;

import java.util.Collection;

public interface AllWordsEvaluator {

  /**
   * Evaluates all words and saves the relevant results to the provided result store.
   *
   * @param words the words to process
   * @param resultStore the result store to add to
   */
  void evaluate(Collection<Word> words, ResultStore resultStore);
}
