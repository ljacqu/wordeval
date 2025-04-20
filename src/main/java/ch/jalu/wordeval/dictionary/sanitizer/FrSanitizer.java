package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.DictionaryUtils;

import java.util.Set;

/**
 * Custom sanitizer for the French dictionary.
 */
public class FrSanitizer extends Sanitizer {

  private static final Set<String> MANUAL_EXCLUSIONS = Set.of(
      "Dᴏꜱꜱᴍᴀɴɴ", "-", "Ångström", "angström", "ångström",
      "brrr", "grrr", "pff", "pfft", "pfut");

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
  protected String removeDelimiters(String crudeWord) {
    String word = super.removeDelimiters(crudeWord);
    int indexPo = word.indexOf(" po:");
    if (indexPo >= 0) {
      return word.substring(0, indexPo);
    }
    return word;
  }

  @Override
  protected String sanitize(String word) {
    if (skipRest) {
      return "";
    } else if ("Δt".equals(word)) {
      skipRest = true;
      return "";
    }

    if (isRomanNumeral(word) || MANUAL_EXCLUSIONS.contains(word)) {
      return "";
    }
    return word;
  }

  // Need to roll out our own logic because there are a lot of entries with grammatical gender, such as:
  // XXXVIe, XXXIIes, XXXIIIe, XLVIIIes
  private static boolean isRomanNumeral(String word) {
    // Search the dictionary with regexp ^[MCLXVI]{2,}e?s?/ and try to exclude as much as possible
    if (word.startsWith("II") || word.startsWith("IV") || word.startsWith("VI")
        || word.startsWith("IX") || word.startsWith("XI") || word.startsWith("XV")
        || word.startsWith("XX") || word.startsWith("XL")) {
      return true;
    }
    return DictionaryUtils.isRomanNumeral(word);
  }
}
