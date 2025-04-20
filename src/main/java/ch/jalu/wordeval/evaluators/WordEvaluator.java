package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;

import java.util.Collection;

/**
 * Simple evaluator which evaluates words individually.
 */
public interface WordEvaluator extends AllWordsEvaluator {

  @Override
  default void evaluate(Collection<Word> words) {
    words.forEach(this::evaluate);
  }

  /**
   * Processes the given word.
   *
   * @param word the word to process
   */
  void evaluate(Word word);

}
