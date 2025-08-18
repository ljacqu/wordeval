package ch.jalu.wordeval.dictionary.hunspell.lineprocessor;

import java.util.Set;

/**
 * Line processor for the Dutch dictionary.
 */
public class NlLineProcessor extends HunspellLineProcessor {

  private static final Set<String> SKIPPED_LINES = Set.of("km\\/h", "km\\/u", "m\\/s", "-/Hp");

  public NlLineProcessor() {
    super(".", "+", " ");
  }

  @Override
  public RootAndAffixes splitWithoutValidation(String line) {
    if (SKIPPED_LINES.contains(line)) {
      return RootAndAffixes.EMPTY;
    }
    return super.splitWithoutValidation(line);
  }

  @Override
  public String transform(String word) {
    return super.transform(word)
        .replace("ĳ", "ij")
        .replace("Ĳ", "IJ");
  }
}
