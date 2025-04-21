package ch.jalu.wordeval.dictionary.hunspell.condition;

import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import lombok.ToString;

import java.util.regex.Pattern;

/**
 * Affix condition for a regexp pattern.
 */
@ToString
public class RegexCondition implements AffixCondition {

  private final Pattern pattern;

  public RegexCondition(String pattern, AffixType affixType) {
    this.pattern = compilePattern(pattern, affixType);
  }

  @Override
  public boolean matches(String word) {
    return pattern.matcher(word).find();
  }

  private static Pattern compilePattern(String pattern, AffixType affixType) {
    String adjustedPattern;
    switch (affixType) {
      case PFX -> adjustedPattern = "^" + pattern;
      case SFX -> adjustedPattern = pattern + "$";
      default -> throw new IllegalStateException("Unexpected value: " + affixType);
    }
    return Pattern.compile(adjustedPattern);
  }
}
