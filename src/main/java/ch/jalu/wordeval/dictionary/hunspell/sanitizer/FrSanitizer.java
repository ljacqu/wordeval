package ch.jalu.wordeval.dictionary.hunspell.sanitizer;

import ch.jalu.wordeval.dictionary.DictionaryUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Custom sanitizer for the French dictionary.
 */
public class FrSanitizer extends HunspellSanitizer {

  private static final String[] MANUAL_EXCLUSIONS = new String[]{
      "Dᴏꜱꜱᴍᴀɴɴ", "-", "Ångström", "angström", "ångström",
      "brrr", "grrr", "pff", "pfft", "pfut"};

  private boolean skipRest = false;

  /**
   * Creates a new sanitizer for the French dictionary.
   */
  public FrSanitizer() {
    super(getSkipSequences());
  }

  @Override
  public RootAndAffixes split(String line) {
    RootAndAffixes rootAndAffixes = super.split(line);
    if (!rootAndAffixes.isEmpty() && skip(rootAndAffixes.root())) {
      return RootAndAffixes.EMPTY;
    }
    return rootAndAffixes;
  }

  private boolean skip(String word) {
    if (skipRest) {
      return true;
    } else if ("Δt".equals(word)) {
      skipRest = true;
      return true;
    }

    if (isRomanNumeral(word) || StringUtils.startsWithAny(word, MANUAL_EXCLUSIONS)) {
      return true;
    }
    return false;
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

  private static String[] getSkipSequences() {
    return new String[]{ ".", "&", "µ", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉", "ᵈ", "ᵉ", "ᵍ", "ˡ", "ᵐ", "ʳ", "ˢ" };
  }
}
