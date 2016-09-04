package ch.jalu.wordeval.dictionary;

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
  public FrSanitizer(DictionarySettings dictionary) {
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
