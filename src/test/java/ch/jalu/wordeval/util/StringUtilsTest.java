package ch.jalu.wordeval.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link StringUtils}.
 */
class StringUtilsTest {

  @Test
  void shouldReturnLastString() {
    // given / when / then
    assertThat(StringUtils.getLastChar("fast"), equalTo('t'));
    assertThat(StringUtils.getLastChar("hello?"), equalTo('?'));
  }
}
