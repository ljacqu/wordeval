package ch.ljacqu.wordeval.language;

/**
 * The type of the letter in an alphabet.
 */
public enum LetterType {

  /** Vowels. */
  VOWELS,

  /** Consonants. */
  CONSONANTS;

  /**
   * Returns the lower-case name of the enum.
   * @return The name of the letter type
   */
  public String getName() {
    return this.toString().toLowerCase();
  }

}
