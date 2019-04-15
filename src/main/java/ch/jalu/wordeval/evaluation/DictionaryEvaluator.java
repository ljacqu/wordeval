package ch.jalu.wordeval.evaluation;

/**
 * Common type for evaluators that gather their results from the dictionary
 */
@Deprecated
public abstract class DictionaryEvaluator<K extends Comparable> extends Evaluator<K> {

  /**
   * Processes a word and adds it to results if it is relevant.
   *
   * @param word the word to check (sanitized: trimmed, all lowercase)
   * @param rawWord the raw form of the word in the dictionary
   */
  public abstract void processWord(String word, String rawWord);

}
