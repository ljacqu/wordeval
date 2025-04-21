package ch.jalu.wordeval.dictionary.hunspell.condition;

import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link RegexCondition}.
 */
class RegexConditionTest {

  @Test
  void shouldCreateStartPattern() {
    // given
    String pattern = "s[tp]";
    AffixType type = AffixType.PFX;

    // when
    RegexCondition condition = new RegexCondition(pattern, type);

    // then
    assertThat(condition.matches("sport"), equalTo(true));
    assertThat(condition.matches("start"), equalTo(true));
    assertThat(condition.matches("restart"), equalTo(false));
    assertThat(condition.matches("fast"), equalTo(false));
    assertThat(condition.matches("short"), equalTo(false));
    assertThat(condition.matches("I"), equalTo(false));
  }

  @Test
  void shouldCreateEndPattern() {
    // given
    String pattern = "r[^dt]";
    AffixType type = AffixType.SFX;

    // when
    RegexCondition condition = new RegexCondition(pattern, type);

    // then
    assertThat(condition.matches("dorm"), equalTo(true));
    assertThat(condition.matches("score"), equalTo(true));
    assertThat(condition.matches("bark"), equalTo(true));
    assertThat(condition.matches("short"), equalTo(false));
    assertThat(condition.matches("board"), equalTo(false));
    assertThat(condition.matches("I"), equalTo(false));
  }
}
