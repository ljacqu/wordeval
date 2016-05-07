package ch.jalu.wordeval.dictionary;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import ch.jalu.wordeval.language.Language;

@SuppressWarnings("javadoc")
public class WordFormsBuilderTest {

  @Test
  public void shouldKeepAdditionalLetters() {
    Language language = newLanguage("da")
        .setAdditionalVowels("æ", "ø", "å");
    WordFormsBuilder builder = new WordFormsBuilder(language);
    String[] words = { "forsøgte erklære trådte", "Å ǿ én býr" };

    String[] result = new String[2];
    result[0] = getForm(builder.computeForms(words[0]), WordForm.NO_ACCENTS);
    result[1] = getForm(builder.computeForms(words[1]), WordForm.NO_ACCENTS);

    String[] expected = { "forsøgte erklære trådte", "å ø en byr" };
    assertThat(result, equalTo(expected));
  }

  @Test
  public void shouldRemoveAllAccentsByDefault() {
    Language language = newLanguage("fr");
    WordFormsBuilder builder = new WordFormsBuilder(language);

    String result = getForm(builder.computeForms("ÉÑÀÇÏÔ"), WordForm.NO_ACCENTS);

    assertThat(result, equalTo("enacio"));
  }

  @Test
  public void shouldUseLocaleForLowerCase() {
    Language language = newLanguage("tr");
    WordFormsBuilder builder = new WordFormsBuilder(language);

    String result = getForm(builder.computeForms("PRINÇE"), WordForm.LOWERCASE);

    assertThat(result, equalTo("prınçe"));
  }

  @Test
  public void shouldComputeWordOnlyForm() {
    Language language = newLanguage("cs")
        .setAdditionalConsonants("č", "ř");
    WordFormsBuilder builder = new WordFormsBuilder(language);

    String result = getForm(builder.computeForms("ČL-OV'ěk-ůŘ"),
        WordForm.NO_ACCENTS_WORD_CHARS_ONLY);

    assertThat(result, equalTo("človekuř"));
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowForEmptyWord() {
    Language language = newLanguage("nl");
    WordFormsBuilder builder = new WordFormsBuilder(language);
    builder.computeForms("");
  }

  private String getForm(String[] forms, WordForm wordForm) {
    return forms[wordForm.ordinal()];
  }

}