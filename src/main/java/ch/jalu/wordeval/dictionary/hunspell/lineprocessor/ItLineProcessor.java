package ch.jalu.wordeval.dictionary.hunspell.lineprocessor;

/**
 * Line processor for the Italian dictionary.
 */
public class ItLineProcessor extends HunspellLineProcessor {

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
