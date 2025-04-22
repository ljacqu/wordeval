package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.condition.AffixCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A Hunspell affix class has one or multiple rules for adding a prefix or suffix to a word.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AffixClass {

  private AffixType type;
  private String flag;
  private boolean crossProduct;
  private final List<AffixRule> rules = new ArrayList<>();

  @Getter
  @RequiredArgsConstructor
  public abstract static class AffixRule {

    protected final String strip;
    private final List<String> continuationClasses;
    private final AffixCondition condition;

    public boolean matches(String word) {
      return condition.matches(word);
    }

    public abstract String applyRule(String word);

    public abstract AffixType getType();

  }

  @Getter
  public static class SuffixRule extends AffixRule {

    private final String suffix;

    public SuffixRule(String strip, String suffix, List<String> continuationClasses, AffixCondition condition) {
      super(strip, continuationClasses, condition);
      this.suffix = suffix;
    }

    @Override
    public String applyRule(String word) {
      return StringUtils.removeEnd(word, strip) + suffix;
    }

    @Override
    public AffixType getType() {
      return AffixType.SFX;
    }
  }

  @Getter
  public static class PrefixRule extends AffixRule {

    private final String prefix;

    public PrefixRule(String strip, String prefix, List<String> continuationClasses, AffixCondition condition) {
      super(strip, continuationClasses, condition);
      this.prefix = prefix;
    }

    @Override
    public String applyRule(String word) {
      return prefix + StringUtils.removeStart(word, strip);
    }

    @Override
    public AffixType getType() {
      return AffixType.PFX;
    }
  }
}
