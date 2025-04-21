package ch.jalu.wordeval.dictionary.hunspell.condition;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link AnyTokenCondition}.
 */
class AnyTokenConditionTest {

  @Test
  void shouldMatchAnyWord() {
    // given / when / then
    assertThat(AnyTokenCondition.INSTANCE.matches("abc"), equalTo(true));
    assertThat(AnyTokenCondition.INSTANCE.matches("a"), equalTo(true));
    assertThat(AnyTokenCondition.INSTANCE.matches("Third"), equalTo(true));
  }
}
