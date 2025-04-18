package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;

import java.util.Collection;

public non-sealed interface AllWordsEvaluator extends Evaluator {

  /**
   * Evaluates all words and saves the relevant results.
   *
   * @param words the words to process
   */
  void evaluate(Collection<Word> words);
}
