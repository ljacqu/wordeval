package ch.jalu.wordeval.dictionary.hunspell.condition;

import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test for {@link CharSequenceConditions}.
 */
class CharSequenceConditionsTest {

  @Test
  void shouldCreateConditionForSingleCharStart() {
    // given
    String pattern = "a";
    AffixType type = AffixType.PFX;

    // when
    AffixCondition condition = CharSequenceConditions.createConditionIfApplicable(pattern, type);

    // then
    assertThat(condition.matches("apple"), equalTo(true));
    assertThat(condition.matches("a"), equalTo(true));
    assertThat(condition.matches("banana"), equalTo(false));
    assertThat(condition.matches("Amb"), equalTo(false));
  }

  @Test
  void shouldCreateConditionForSingleCharEnd() {
    // given
    String pattern = "t";
    AffixType type = AffixType.SFX;

    // when
    AffixCondition condition = CharSequenceConditions.createConditionIfApplicable(pattern, type);

    // then
    assertThat(condition.matches("bat"), equalTo(true));
    assertThat(condition.matches("t"), equalTo(true));
    assertThat(condition.matches("goal"), equalTo(false));
    assertThat(condition.matches("FAST"), equalTo(false));
  }

  @Test
  void shouldCreateConditionForPfxSequence() {
    // given
    String pattern = "anti";
    AffixType type = AffixType.PFX;

    // when
    AffixCondition condition = CharSequenceConditions.createConditionIfApplicable(pattern, type);

    // then
    assertThat(condition.matches("antisocial"), equalTo(true));
    assertThat(condition.matches("anti"), equalTo(true));
    assertThat(condition.matches("antsy"), equalTo(false));
    assertThat(condition.matches("banana"), equalTo(false));
    assertThat(condition.matches("Antilles"), equalTo(false));
  }

  @Test
  void shouldCreateConditionForSfxSequence() {
    // given
    String pattern = "sh";
    AffixType type = AffixType.SFX;

    // when
    AffixCondition condition = CharSequenceConditions.createConditionIfApplicable(pattern, type);

    // then
    assertThat(condition.matches("ash"), equalTo(true));
    assertThat(condition.matches("ashes"), equalTo(false));
    assertThat(condition.matches("plane"), equalTo(false));
  }

  @Test
  void shouldReturnNullForUnsupportedPatterns() {
    // given / when / then
    assertNull(CharSequenceConditions.createConditionIfApplicable("b[wy]", AffixType.SFX));
    assertNull(CharSequenceConditions.createConditionIfApplicable("es?", AffixType.SFX));
    assertNull(CharSequenceConditions.createConditionIfApplicable(".", AffixType.SFX));
    assertNull(CharSequenceConditions.createConditionIfApplicable("[^a]", AffixType.SFX));
  }
}
