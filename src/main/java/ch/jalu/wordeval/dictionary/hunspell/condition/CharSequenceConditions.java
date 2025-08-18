package ch.jalu.wordeval.dictionary.hunspell.condition;

import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.regex.Pattern;

import static ch.jalu.wordeval.util.StringUtils.getLastChar;

/**
 * Creates affix conditions for character sequences.
 */
public final class CharSequenceConditions {

  private static final Pattern CHAR_SEQUENCE_PATTERN = Pattern.compile("\\w+");

  private CharSequenceConditions() {
  }

  /**
   * Creates an affix condition for the given pattern, if possible by this class (when the pattern is a simple
   * sequence of characters).
   *
   * @param pattern the pattern to process
   * @param type the affix type the condition is for
   * @return condition if possible, null otherwise
   */
  public static AffixCondition createConditionIfApplicable(String pattern, AffixType type) {
    if (CHAR_SEQUENCE_PATTERN.matcher(pattern).matches()) {
      if (type == AffixType.PFX) {
        return pattern.length() == 1
            ? new StartsWithSingleChar(pattern.charAt(0))
            : new StartsWithSequence(pattern);
      } else { // SFX
        return pattern.length() == 1
            ? new EndsWithSingleChar(pattern.charAt(0))
            : new EndsWithSequence(pattern);
      }
    }
    return null;
  }

  @ToString
  @RequiredArgsConstructor
  private static class StartsWithSingleChar implements AffixCondition {

    private final char expectedChar;

    @Override
    public boolean matches(String word) {
      return word.charAt(0) == expectedChar;
    }

    @Override
    public String getPatternText() {
      return String.valueOf(expectedChar);
    }
  }

  @ToString
  @RequiredArgsConstructor
  private static class EndsWithSingleChar implements AffixCondition {

    private final char expectedChar;

    @Override
    public boolean matches(String word) {
      return getLastChar(word) == expectedChar;
    }

    @Override
    public String getPatternText() {
      return String.valueOf(expectedChar);
    }
  }

  @ToString
  @RequiredArgsConstructor
  private static class StartsWithSequence implements AffixCondition {

    private final String sequence;

    @Override
    public boolean matches(String word) {
      return word.startsWith(sequence);
    }

    @Override
    public String getPatternText() {
      return sequence;
    }
  }

  @ToString
  @RequiredArgsConstructor
  private static class EndsWithSequence implements AffixCondition {

    private final String sequence;

    @Override
    public boolean matches(String word) {
      return word.endsWith(sequence);
    }

    @Override
    public String getPatternText() {
      return sequence;
    }
  }
}
