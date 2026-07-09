package ch.jalu.wordeval.dictionary.hunspell.lineprocessor;

import ch.jalu.wordeval.dictionary.DictionaryUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Line processor for Hunspell dictionary which can be extended.
 */
public class HunspellLineProcessor {

  private final String[] skipSequences;

  public HunspellLineProcessor(String... skipSequences) {
    this.skipSequences = skipSequences;
  }

  public RootAndAffixes split(String line) {
    RootAndAffixes rootAndAffixes = splitWithoutValidation(line);
    if (shouldSkipRoot(rootAndAffixes.root())) {
      return RootAndAffixes.EMPTY;
    }
    return rootAndAffixes;
  }

  protected boolean shouldSkipRoot(String root) {
    return StringUtils.containsAny(root, skipSequences)
        || root.chars().anyMatch(ch -> ch >= '0' && ch <= '9')
        || DictionaryUtils.isRomanNumeral(root);
  }

  public RootAndAffixes splitWithoutValidation(String line) {
    int indexOfSlash = line.indexOf('/');
    if (indexOfSlash < 0) {
      // No slash - word has no affixes
      return new RootAndAffixes(line, "");
    }
    if (line.indexOf('\\') > 0) {
      // Backslashes can be used to escape the delimiter. Only few dictionaries use it and typically for words that
      // aren't interesting to wordeval, so force manual handling of these words so we don't have to complicate our
      // parsing logic.
      throw new IllegalArgumentException("Backslash found in line: " + line);
    }

    String root = line.substring(0, indexOfSlash);
    String affixFlags = extractAffixFlags(line.substring(indexOfSlash + 1));
    return new RootAndAffixes(root, affixFlags);
  }

  public String transform(String word) {
    return word.replace('–', '-');
  }

  /**
   * Returns the affix flags indicated in the "meta part" of the line, i.e. the section after the slash separating
   * the word.
   *
   * @param metaPart the part with the affixes
   * @return all affix flags in the given meta part
   */
  private String extractAffixFlags(String metaPart) {
    int indexFirstWhitespace = -1;
    for (int i = 0; i < metaPart.length(); ++i) {
      if (Character.isWhitespace(metaPart.charAt(i))) {
        indexFirstWhitespace = i;
        break;
      }
    }

    return indexFirstWhitespace >= 0 ? metaPart.substring(0, indexFirstWhitespace) : metaPart;
  }
}
