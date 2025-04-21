package ch.jalu.wordeval.dictionary.hunspell;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AffixType}.
 */
class AffixTypeTest {

  @Test
  void shouldMapFromText() {
    // given / when / then
    assertThat(AffixType.fromString("PFX"), equalTo(AffixType.PFX));
    assertThat(AffixType.fromString("SFX"), equalTo(AffixType.SFX));
  }

  @Test
  void shouldThrowForUnknownName() {
    // given / when
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> AffixType.fromString("ZIP"));

    // then
    assertThat(ex.getMessage(), equalTo("Invalid affix type: ZIP"));
  }
}
