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
  private AffixFlagType flagType = AffixFlagType.SINGLE;

  @Getter
  @Setter
  private String needAffixFlag;

  @Getter
  @Setter
  private String forbiddenWordClass;

  private final List<ParsedAffixClass> classes = new ArrayList<>();
  private ParsedAffixClass currentClass;

  public void addAffixClass(ParsedAffixClass affixClass) {
    classes.add(affixClass);
    currentClass = affixClass;
  }

  public void addRuleToCurrentClass(ParsedAffixClass.Rule rule) {
    currentClass.rules.add(rule);
  }

  public List<ParsedAffixClass> getAffixClasses() {
    // Return an immutable list so we don't accidentally add a rule manually, since it needs to happen via #addRule
    return List.copyOf(classes);
  }
}
