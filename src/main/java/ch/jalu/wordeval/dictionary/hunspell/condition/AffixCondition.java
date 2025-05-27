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

  /**
   * Returns a text of the pattern the condition checks. This is for debugging/visualization and
   * is not parseable or consistent among all implementations! The string returned by this method
   * does not necessarily correspond to the condition in the .aff file it originated from, nor is
   * it guaranteed to be in a valid format for an .aff file.
   *
   * @return text showing the pattern of this condition
   */
  String getPatternText();

}
