package ch.jalu.wordeval.language;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Alphabet}.
 */
public class AlphabetTest {

  @Test
  public void shouldRemoveDiacritics() {
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
  public void shouldRemoveDiacriticsForCyrillic() {
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
