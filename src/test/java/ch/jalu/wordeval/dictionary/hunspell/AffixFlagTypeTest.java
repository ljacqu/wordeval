package ch.jalu.wordeval.dictionary.hunspell;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link AffixFlagType}.
 */
class AffixFlagTypeTest {

  @Test
  void shouldMapFromText() {
    // given / when / then
    assertThat(AffixFlagType.fromAffixFileString("UTF-8"), equalTo(AffixFlagType.SINGLE));
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

  @Test
  void shouldSplitToSingleChars() {
    // given
    String list = "ABD";

    // when
    List<String> result = AffixFlagType.SINGLE.split(list);

    // then
    assertThat(result, contains("A", "B", "D"));
  }

  @Test
  void shouldSplitInPairs() {
    // given
    String list = "AbP0C'D?";

    // when
    List<String> result = AffixFlagType.LONG.split(list);

    // then
    assertThat(result, contains("Ab", "P0", "C'", "D?"));
  }

  @Test
  void shouldSplitByCommas() {
    // given
    String list = "12,49,1142,73";

    // when
    List<String> result = AffixFlagType.NUMBER.split(list);

    // then
    assertThat(result, contains("12", "49", "1142", "73"));
  }

  @Test
  void shouldReturnSingleAffix() {
    // given / when / then
    assertThat(AffixFlagType.SINGLE.split("F"), contains("F"));
    assertThat(AffixFlagType.LONG.split("F0"), contains("F0"));
    assertThat(AffixFlagType.NUMBER.split("182"), contains("182"));
  }
}
