package ch.jalu.wordeval.dictionary.hunspell.condition;

import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test for {@link SingleCharCondition}.
 */
class SingleCharConditionTest {

  @Test
  void shouldCreateConditionForStart() {
    // given
    String pattern = "[dt]";
    AffixType type = AffixType.PFX;

    // when
    AffixCondition condition = SingleCharCondition.createConditionIfApplicable(pattern, type);

    // then
    assertThat(condition.matches("desk"), equalTo(true));
    assertThat(condition.matches("tusk"), equalTo(true));
    assertThat(condition.matches("pesky"), equalTo(false));
    assertThat(condition.matches("Turin"), equalTo(false));
  }

  @Test
  void shouldCreateConditionForEnd() {
    // given
    String pattern = "[ae]";
    AffixType type = AffixType.SFX;

    // when
    AffixCondition condition = SingleCharCondition.createConditionIfApplicable(pattern, type);

    // then
    assertThat(condition.matches("gala"), equalTo(true));
    assertThat(condition.matches("tape"), equalTo(true));
    assertThat(condition.matches("fast"), equalTo(false));
    assertThat(condition.matches("NASA"), equalTo(false));
  }

  @Test
  void shouldCreateNegatedConditionForStart() {
    // given
    String pattern = "[^bp]";
    AffixType type = AffixType.PFX;

    // when
    AffixCondition condition = SingleCharCondition.createConditionIfApplicable(pattern, type);

    // then
    assertThat(condition.matches("hack"), equalTo(true));
    assertThat(condition.matches("wack"), equalTo(true));
    assertThat(condition.matches("back"), equalTo(false));
    assertThat(condition.matches("pack"), equalTo(false));
    assertThat(condition.matches("BP"), equalTo(true));
  }

  @Test
  void shouldCreateNegatedConditionForEnd() {
    // given
    String pattern = "[^kg]";
    AffixType type = AffixType.SFX;

    // when
    AffixCondition condition = SingleCharCondition.createConditionIfApplicable(pattern, type);

    // then
    assertThat(condition.matches("such"), equalTo(true));
    assertThat(condition.matches("kind"), equalTo(true));
    assertThat(condition.matches("suck"), equalTo(false));
    assertThat(condition.matches("king"), equalTo(false));
  }

  @Test
  void shouldReturnNullForUnsupportedPatterns() {
    // given / when / then
    assertNull(SingleCharCondition.createConditionIfApplicable("b[wy]", AffixType.SFX));
    assertNull(SingleCharCondition.createConditionIfApplicable("k", AffixType.SFX));
    assertNull(SingleCharCondition.createConditionIfApplicable("es?", AffixType.SFX));
    assertNull(SingleCharCondition.createConditionIfApplicable(".", AffixType.SFX));
    assertNull(SingleCharCondition.createConditionIfApplicable("[a-z]", AffixType.SFX));
  }

  @Test
  void shouldReturnPatternText() {
    // given / when / then
    assertThat(SingleCharCondition.createConditionIfApplicable("[ab]", AffixType.PFX).getPatternText(), equalTo("[ab]"));
    assertThat(SingleCharCondition.createConditionIfApplicable("[def]", AffixType.SFX).getPatternText(), equalTo("[def]"));
    assertThat(SingleCharCondition.createConditionIfApplicable("[^abc]", AffixType.PFX).getPatternText(), equalTo("[^abc]"));
    assertThat(SingleCharCondition.createConditionIfApplicable("[^de]", AffixType.SFX).getPatternText(), equalTo("[^de]"));
  }
}
