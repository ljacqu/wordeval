package ch.jalu.wordeval.dictionary.hunspell.parser;

import ch.jalu.wordeval.dictionary.hunspell.AffixFlagType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Parse result of an .aff file.
 */
@ToString
public class ParsedAffixes {

  @Getter
  @Setter
  private AffixFlagType flagType;
  private final List<ParsedRule> rules = new ArrayList<>();
  private ParsedRule currentRule;

  public void addRule(ParsedRule rule) {
    rules.add(rule);
    currentRule = rule;
  }

  public void addEntryToCurrentRule(ParsedRule.RuleEntry entry) {
    currentRule.rules.add(entry);
  }

  public List<ParsedRule> getRules() {
    // Return an immutable list so we don't accidentally add a rule manually, since it needs to happen via #addRule
    return List.copyOf(rules);
  }
}
