package ch.jalu.wordeval.dictionary.hunspell;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Data from a parsed Hunspell .aff file.
 */
@Getter
@Setter
public class HunspellAffixes {

  private AffixFlagType flagType;
  private Map<String, AffixClass> affixClassesByFlag;

  public Stream<AffixClass.AffixRule> streamThroughMatchingRules(String word, String affixFlag) {
    AffixClass affixClass = affixClassesByFlag.get(affixFlag);
    if (affixClass == null) {
      // todo: log this?
      return Stream.empty();
    }
    return affixClass.getRules().stream()
        .filter(rule -> rule.matches(word));
  }
}
