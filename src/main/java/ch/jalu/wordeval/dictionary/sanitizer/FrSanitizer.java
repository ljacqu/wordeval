package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.dictionary.Dictionary;

/**
 * Custom sanitizer for the French dictionary.
 */
public class FrSanitizer extends Sanitizer {
  
  private boolean skipRest = false;

  /**
   * Creates a new sanitizer for the French dictionary.
   *
   * @param dictionary the dictionary
   */
  public FrSanitizer(Dictionary dictionary) {
    super(dictionary);
  }
  
  @Override
  protected String customSanitize(String word) {
    if (skipRest) {
      return "";
    } else if ("Δt".equals(word)) {
      skipRest = true;
      return "";
    }
    return word;
  }

}
