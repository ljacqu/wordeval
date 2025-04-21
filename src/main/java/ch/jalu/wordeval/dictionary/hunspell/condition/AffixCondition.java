package ch.jalu.wordeval.dictionary.hunspell.condition;

/**
 * Condition for when an affix rule entry is applicable.
 */
public interface AffixCondition {

  /**
   * Returns whether this condition applies to the given word.
   *
   * @param word the word to check
   * @return true if the condition matches, false otherwise
   */
  boolean matches(String word);

}
