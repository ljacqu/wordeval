package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.condition.AffixCondition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class AffixRule {

  protected final String strip;
  protected final String affix;
  private final List<String> continuationClasses;
  private final AffixCondition condition;
  private final boolean crossProduct;

  public abstract String applyRule(String word);

  public abstract AffixType getType();

  public boolean matches(String word) {
    return condition.matches(word);
  }

  public static class SuffixRule extends AffixRule {

    public SuffixRule(String strip, String suffix, List<String> continuationClasses,
                      AffixCondition condition, boolean crossProduct) {
      super(strip, suffix, continuationClasses, condition, crossProduct);
    }

    @Override
    public String applyRule(String word) {
      if (strip.isEmpty()) {
        return word + affix;
      }

      String strippedWord = StringUtils.removeEnd(word, strip);
      if (strippedWord.equals(word)) {
        return null;
      }
      return strippedWord + affix;
    }

    @Override
    public AffixType getType() {
      return AffixType.SFX;
    }
  }

  public static class PrefixRule extends AffixRule {

    public PrefixRule(String strip, String prefix, List<String> continuationClasses,
                      AffixCondition condition, boolean crossProduct) {
      super(strip, prefix, continuationClasses, condition, crossProduct);
    }

    @Override
    public String applyRule(String word) {
      if (strip.isEmpty()) {
        return affix + word;
      }

      String strippedWord = StringUtils.removeStart(word, strip);
      if (strippedWord.equals(word)) {
        return null;
      }
      return affix + strippedWord;
    }

    @Override
    public AffixType getType() {
      return AffixType.PFX;
    }
  }
}
