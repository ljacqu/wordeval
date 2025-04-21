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

  public interface AffixRule {

    boolean matches(String word);

    String applyRule(String word);

  }

  @Getter
  @RequiredArgsConstructor
  public static class SuffixRule implements AffixRule {

    private final String strip;
    private final String suffix;
    private final AffixCondition condition;

    @Override
    public boolean matches(String word) {
      return condition.matches(word);
    }

    @Override
    public String applyRule(String word) {
      return StringUtils.removeEnd(word, strip) + suffix;
    }
  }

  @Getter
  @RequiredArgsConstructor
  public static class PrefixRule implements AffixRule {

    private final String strip;
    private final String prefix;
    private final AffixCondition condition;

    @Override
    public boolean matches(String word) {
      return condition.matches(word);
    }

    @Override
    public String applyRule(String word) {
      return prefix + StringUtils.removeStart(word, strip);
    }
  }
}
