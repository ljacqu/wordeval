package ch.jalu.wordeval.dictionary.hunspell.lineprocessor;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Line processor for the French dictionary.
 */
public class FrLineProcessor extends HunspellLineProcessor {

  private static final String[] MANUAL_EXCLUSIONS = new String[]{
      "Dᴏꜱꜱᴍᴀɴɴ", "-", "Ångström", "angström", "ångström",
      "brrr", "grrr", "pff", "pfft", "pfut"};

  private boolean skipRest = false;

  /**
   * Constructor.
   */
  public FrLineProcessor() {
    super(getSkipSequences());
  }

  @Override
  public RootAndAffixes splitWithoutValidation(String line) {
    RootAndAffixes rootAndAffixes = super.splitWithoutValidation(line);
    if (rootAndAffixes.root().contains(" ")) {
      Preconditions.checkState(rootAndAffixes.affixFlags().isEmpty(), "Line '%s' has space but affix flags", line);
      return rootAndAffixes.withNewRoot(StringUtils.substringBefore(rootAndAffixes.root(), " "));
    }
    return rootAndAffixes;
  }

  @Override
  protected boolean shouldSkipRoot(String root) {
    if (super.shouldSkipRoot(root)) {
      return true;
    }
    if (skipRest) {
      return true;
    } else if ("Δt".equals(root)) {
      skipRest = true;
      return true;
    }

    if (isRomanNumeral(root) || StringUtils.startsWithAny(root, MANUAL_EXCLUSIONS)) {
      return true;
    }
    return false;
  }

  // Need to roll out our own logic because there are a lot of entries with grammatical gender, such as:
  // XXXVIe, XXXIIes, XXXIIIe, XLVIIIes
  // Note: DictionaryUtils.isRomanNumeral(word) was already checked if we are in this method
  private static boolean isRomanNumeral(String word) {
    // Search the dictionary with regexp ^[MCLXVI]{2,}e?s?/ and try to exclude as much as possible
    return word.startsWith("II") || word.startsWith("IV") || word.startsWith("VI")
        || word.startsWith("IX") || word.startsWith("XI") || word.startsWith("XV")
        || word.startsWith("XX") || word.startsWith("XL");
  }

  private static String[] getSkipSequences() {
    return new String[]{ ".", "&", "µ", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉", "ᵈ", "ᵉ", "ᵍ", "ˡ", "ᵐ", "ʳ", "ˢ" };
  }
}
