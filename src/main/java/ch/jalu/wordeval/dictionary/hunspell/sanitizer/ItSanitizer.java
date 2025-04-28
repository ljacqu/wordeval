package ch.jalu.wordeval.dictionary.hunspell.sanitizer;

/**
 * Custom sanitizer for the Italian dictionary.
 */
public class ItSanitizer extends HunspellSanitizer {

  @Override
  public boolean skipLine(String line) {
    if (line.startsWith("Copyright")) {
      return true;
    } else if ("ziiiziiizxxivziiizmmxi".equals(line)) {
      return true;
    }
    return super.skipLine(line);
  }
}
