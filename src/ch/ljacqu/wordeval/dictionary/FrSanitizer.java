package ch.ljacqu.wordeval.dictionary;

import ch.ljacqu.wordeval.language.Language;

/**
 * Custom sanitizer for the French dictionary.
 */
public class FrSanitizer extends Sanitizer {
  
  private boolean skipRest = false;

  /**
   * Creates a new sanitizer for the French dictionary.
   */
  public FrSanitizer() {
    super(Language.get("fr"), initSettings());
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
  
  private static DictionarySettings initSettings() {
    return new DictionarySettings("fr")
      .setDelimiters('/', '\t')
      .setSkipSequences(".", "&", "µ");
  }

}
