package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.dictionary.WordForm;

/**
 * Common type for evaluators that gather their results from the dictionary
 * (as opposed to {@link PostEvaluator} implementations).
 */
public abstract class DictionaryEvaluator<K extends Comparable> extends Evaluator<K> {

  /**
   * Processes a word and adds it to results if it is relevant.
   *
   * @param word the word to check (sanitized: trimmed, all lowercase)
   * @param rawWord the raw form of the word in the dictionary
   */
  public abstract void processWord(String word, String rawWord);


  /**
   * Returns the desired form of the word to process.
   *
   * @return the word form
   */
  public WordForm getWordForm() {
    return WordForm.LOWERCASE;
  }

}
