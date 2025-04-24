package ch.jalu.wordeval.dictionary.hunspell;

import com.google.common.collect.ListMultimap;
import lombok.Getter;
import lombok.Setter;

import java.util.stream.Stream;

/**
 * Data from a parsed Hunspell .aff file.
 */
@Getter
@Setter
public class HunspellAffixes {

  private AffixFlagType flagType;
  private String needAffixFlag;
  private String forbiddenWordClass;
  private ListMultimap<String, AffixRule> affixRulesByFlag;

  public Stream<AffixRule> streamThroughMatchingRules(String word, String affixFlag) {
    return affixRulesByFlag.get(affixFlag).stream()
        .filter(rule -> rule.matches(word));
  }
}
