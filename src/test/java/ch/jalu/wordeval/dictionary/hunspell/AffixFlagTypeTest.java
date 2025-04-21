package ch.jalu.wordeval.dictionary.hunspell;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AffixFlagType}.
 */
class AffixFlagTypeTest {

  @Test
  void shouldMapFromText() {
    // given / when / then
    assertThat(AffixFlagType.fromAffixFileString("long"), equalTo(AffixFlagType.LONG));
    assertThat(AffixFlagType.fromAffixFileString("num"), equalTo(AffixFlagType.NUMBER));
  }

  @Test
  void shouldThrowForUnknownName() {
    // given / when
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> AffixFlagType.fromAffixFileString("bogus"));

    // then
    assertThat(ex.getMessage(), equalTo("Unknown affix flag type: bogus"));
  }
}
