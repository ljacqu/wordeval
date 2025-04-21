package ch.jalu.wordeval.dictionary.hunspell.condition;

import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ch.jalu.wordeval.util.StringUtils.getLastChar;

/**
 * Creates affix conditions for a single character, which is a range of characters.
 */
public final class SingleCharCondition {

  private static final Pattern MATCHING_RULES = Pattern.compile("\\[(\\^)?(\\w+)]");

  private SingleCharCondition() {
  }

  /**
   * Creates an affix condition for the given pattern, if supported by this class (needs to be a range for
   * a single character).
   *
   * @param pattern the pattern to process
   * @param type the affix type the condition is for
   * @return condition if possible, null otherwise
   */
  public static AffixCondition createConditionIfApplicable(String pattern, AffixType type) {
    Matcher matcher = MATCHING_RULES.matcher(pattern);
    if (matcher.matches()) {
      boolean negate = StringUtils.isNotEmpty(matcher.group(1)); // ^ or null
      Set<Character> chars = matcher.group(2).chars()
          .mapToObj(i -> (char) i)
          .collect(Collectors.toSet());

      return type == AffixType.PFX
          ? new StartsWith(chars, negate)
          : new EndsWith(chars, negate);
    }
    return null;
  }

  private static String constructPatternText(Set<Character> characters, boolean negate) {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    if (negate) {
      sb.append('^');
    }
    characters.stream()
        .sorted()
        .forEach(sb::append);
    sb.append(']');
    return sb.toString();
  }

  @ToString
  @RequiredArgsConstructor
  private static final class EndsWith implements AffixCondition {

    private final Set<Character> characters;
    private final boolean negate;

    @Override
    public boolean matches(String word) {
      char lastChar = getLastChar(word);
      return characters.contains(lastChar) != negate;
    }

    @Override
    public String getPatternText() {
      return constructPatternText(characters, negate);
    }
  }

  @ToString
  @RequiredArgsConstructor
  private static final class StartsWith implements AffixCondition {

    private final Set<Character> characters;
    private final boolean negate;

    @Override
    public boolean matches(String word) {
      char firstChar = word.charAt(0);
      return characters.contains(firstChar) != negate;
    }

    @Override
    public String getPatternText() {
      return constructPatternText(characters, negate);
    }
  }
}
