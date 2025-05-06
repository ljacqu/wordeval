package ch.jalu.wordeval.dictionary.hunspell.sanitizer;

/**
 * Custom sanitizer for the Italian dictionary.
 */
public class ItSanitizer extends HunspellSanitizer {

  @Override
  public RootAndAffixes split(String line) {
    if (line.startsWith("Copyright")) {
      return RootAndAffixes.EMPTY;
    } else if ("ziiiziiizxxivziiizmmxi".equals(line)) {
      return RootAndAffixes.EMPTY;
    }
    return super.split(line);
  }
}
