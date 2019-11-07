package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.language.Language;
import org.junit.jupiter.api.Test;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Test for {@link WordFactory}.
 */
class WordFactoryTest {

  @Test
  void shouldKeepAdditionalLetters() {
    // given
    Language language = newLanguage("da")
        .additionalVowels("æ", "ø", "å")
        .build();
    String[] words = { "forsøgte erklære trådte", "Å ǿ én býr" };
    WordFactory builder = new WordFactory(language);

    // when
    Word[] result = new Word[2];
    result[0] = builder.createWordObject(words[0]);
    result[1] = builder.createWordObject(words[1]);

    // then
    assertThat(result[0].getWithoutAccents(), equalTo("forsøgte erklære trådte"));
    assertThat(result[1].getWithoutAccents(), equalTo("å ø en byr"));
  }

  @Test
  void shouldRemoveAllAccentsByDefault() {
    // given
    Language language = newLanguage("fr").build();
    WordFactory builder = new WordFactory(language);

    // when
    Word result = builder.createWordObject("ÉÑÀÇÏÔ");

    // then
    assertThat(result.getWithoutAccents(), equalTo("enacio"));
  }

  @Test
  void shouldUseLocaleForLowerCase() {
    // given
    Language language = newLanguage("tr").build();
    WordFactory builder = new WordFactory(language);

    // when
    Word result = builder.createWordObject("PRINÇE");

    // then
    assertThat(result.getLowercase(), equalTo("prınçe"));
  }

  @Test
  void shouldComputeWordOnlyForm() {
    // given
    Language language = newLanguage("cs")
        .additionalConsonants("č", "ř")
        .build();
    WordFactory builder = new WordFactory(language);

    // when
    Word result = builder.createWordObject("ČL-OV'ěk-ůŘ");

    // then
    assertThat(result.getWithoutAccentsWordCharsOnly(), equalTo("človekuř"));
  }

  @Test
  void shouldThrowForEmptyWord() {
    // given
    Language language = newLanguage("nl").build();
    WordFactory builder = new WordFactory(language);

    // when / then
    assertThrows(IllegalArgumentException.class, () -> builder.createWordObject(""));
  }

}
