package ch.jalu.wordeval.dictionary.hunspell;

import com.google.common.collect.ListMultimap;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Data from a Hunspell affix file (*.aff).
 */
@Getter
@Setter
public class HunspellAffixes {

  private AffixFlagType flagType;
  private String needAffixFlag;
  private Set<String> forbiddenWordClasses = new HashSet<>();
  private ListMultimap<String, AffixRule> affixRulesByFlag;

  /**
   * Returns a stream of all rules associated with the affix flag whose condition match with the word.
   * <p>
   * Note that a rule might still not apply as its result will be {@code null} when applied to the word.
   *
   * @param word the word to check with
   * @param affixFlag the flag for which the rules should be processed
   * @return stream of all rules for which the word matches their condition
   */
  public Stream<AffixRule> streamThroughMatchingRules(String word, String affixFlag) {
    return affixRulesByFlag.get(affixFlag).stream()
        .filter(rule -> rule.matches(word));
  }

  /**
   * Returns whether any of the given flags signals that the word is forbidden, i.e. that it is not
   * a valid word and that it shouldn't be considered.
   *
   * @param affixFlags the flags to inspect
   * @return true if any of the flags is "forbidden"
   */
  public boolean containsForbiddenFlag(List<String> affixFlags) {
    for (String flag : affixFlags) {
      if (forbiddenWordClasses.contains(flag)) {
        return true;
      }
    }
    return false;
  }
}
