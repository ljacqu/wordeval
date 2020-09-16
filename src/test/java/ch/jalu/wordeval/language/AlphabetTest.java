package ch.jalu.wordeval.language;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link Alphabet}.
 */
class AlphabetTest {

  @Test
  void shouldRemoveDiacritics() {
    // given
    String[] words = {
        "křižáků", "nőstényét", "iš vėlyvojo Jų", "mogą trwać występy koreańskich że", "požiūriu" };

    // when
    String[] result = Arrays.stream(words)
        .map(Alphabet.LATIN::removeAccents)
        .toArray(String[]::new);

    // then
    String[] expected = {
        "krizaku", "nostenyet", "is velyvojo Ju", "moga trwac wystepy koreanskich ze", "poziuriu" };
    assertThat(result, equalTo(expected));
  }

  @Test
  void shouldRemoveDiacriticsForCyrillic() {
    // given
    String[] words = { "ѝ", "призёр", "Менько́в", "аўтар", "куќата", "військової" };

    // when
    String[] result = Arrays.stream(words)
        .map(Alphabet.CYRILLIC::removeAccents)
        .toArray(String[]::new);

    // then
    String[] expected = { "и", "призёр", "Меньков", "аўтар", "куќата", "військової" };
    assertThat(result, equalTo(expected));
  }
}
