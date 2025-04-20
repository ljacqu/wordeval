package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.dictionary.Dictionary;

/**
 * Custom sanitizer for the Italian dictionary.
 */
public class ItSanitizer extends Sanitizer {

  /**
   * Constructor.
   *
   * @param dictionary the dictionary settings
   */
  public ItSanitizer(Dictionary dictionary) {
    super(dictionary);
  }

  @Override
  protected String sanitize(String word) {
    if (word.startsWith("Copyright")) {
      return null;
    } else if ("ziiiziiizxxivziiizmmxi".equals(word)) {
      return null;
    }
    return super.sanitize(word);
  }
}
