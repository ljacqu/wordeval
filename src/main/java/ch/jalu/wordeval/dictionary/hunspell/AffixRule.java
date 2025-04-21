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
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AffixRule {

  private AffixType type;
  private String flag;
  private boolean crossProduct;
  private final List<AffixRuleEntry> rules = new ArrayList<>();

  public Stream<AffixRuleEntry> streamThroughMatchingEntries(String word) {
    return rules.stream()
        .filter(entry -> entry.condition.matches(word));
  }

  @RequiredArgsConstructor
  public static abstract class AffixRuleEntry {

    private final AffixCondition condition;

    public abstract String applyRule(String word);

  }

  public static class SuffixRuleEntry extends AffixRuleEntry {

    private final String strip;
    private final String append;

    SuffixRuleEntry(AffixCondition condition, String strip, String append) {
      super(condition);
      this.strip = strip;
      this.append = append;
    }

    @Override
    public String applyRule(String word) {
      return StringUtils.removeEnd(word, strip) + append;
    }
  }

  public static class PrefixRuleEntry extends AffixRuleEntry {

    private final String strip;
    private final String append;

    PrefixRuleEntry(AffixCondition condition, String strip, String append) {
      super(condition);
      this.strip = strip;
      this.append = append;
    }

    @Override
    public String applyRule(String word) {
      return append + StringUtils.removeStart(word, strip);
    }
  }
}
