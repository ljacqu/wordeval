package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.language.Language;
import org.junit.jupiter.api.Test;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link WordFormsBuilder}.
 */
class WordFormsBuilderTest {

  @Test
  void shouldKeepAdditionalLetters() {
    // given
    Language language = newLanguage("da")
        .additionalVowels("æ", "ø", "å")
        .build();
    String[] words = { "forsøgte erklære trådte", "Å ǿ én býr" };
    WordFormsBuilder builder = new WordFormsBuilder(language);

    // when
    Word[] result = new Word[2];
    result[0] = builder.computeForms(words[0]);
    result[1] = builder.computeForms(words[1]);

    // then
    assertThat(result[0].getForm(WordForm.NO_ACCENTS), equalTo("forsøgte erklære trådte"));
    assertThat(result[1].getForm(WordForm.NO_ACCENTS), equalTo("å ø en byr"));
  }

  @Test
  void shouldRemoveAllAccentsByDefault() {
    // given
    Language language = newLanguage("fr").build();
    WordFormsBuilder builder = new WordFormsBuilder(language);

    // when
    Word result = builder.computeForms("ÉÑÀÇÏÔ");

    // then
    assertThat(result.getForm(WordForm.NO_ACCENTS), equalTo("enacio"));
  }

  @Test
  void shouldUseLocaleForLowerCase() {
    // given
    Language language = newLanguage("tr").build();
    WordFormsBuilder builder = new WordFormsBuilder(language);

    // when
    Word result = builder.computeForms("PRINÇE");

    // then
    assertThat(result.getForm(WordForm.LOWERCASE), equalTo("prınçe"));
  }

  @Test
  void shouldComputeWordOnlyForm() {
    // given
    Language language = newLanguage("cs")
        .additionalConsonants("č", "ř")
        .build();
    WordFormsBuilder builder = new WordFormsBuilder(language);

    // when
    Word result = builder.computeForms("ČL-OV'ěk-ůŘ");

    // then
    assertThat(result.getForm(WordForm.NO_ACCENTS_WORD_CHARS_ONLY), equalTo("človekuř"));
  }

  @Test
  void shouldThrowForEmptyWord() {
    Language language = newLanguage("nl").build();
    WordFormsBuilder builder = new WordFormsBuilder(language);
    assertThrows(IllegalStateException.class, () -> builder.computeForms(""));
  }

}
