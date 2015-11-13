package ch.ljacqu.wordeval.dictionary;

import static ch.ljacqu.wordeval.dictionary.WordForm.LOWERCASE;
import static ch.ljacqu.wordeval.dictionary.WordForm.NO_ACCENTS;
import static ch.ljacqu.wordeval.dictionary.WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import ch.ljacqu.wordeval.language.Language;

@SuppressWarnings("javadoc")
public class WordFormsBuilderTest {

  @Test
  public void shouldKeepAdditionalLetters() {
    Language language = new Language("da", LATIN)
        .setAdditionalVowels("æ", "ø", "å");
    WordFormsBuilder builder = new WordFormsBuilder(language);
    String[] words = { "forsøgte erklære trådte", "Å ǿ én býr" };

    String[] result = new String[2];
    result[0] = getForm(builder.computeForms(words[0]), NO_ACCENTS);
    result[1] = getForm(builder.computeForms(words[1]), NO_ACCENTS);

    String[] expected = { "forsøgte erklære trådte", "å ø en byr" };
    assertThat(result, equalTo(expected));
  }

  @Test
  public void shouldRemoveAllAccentsByDefault() {
    Language language = new Language("fr", LATIN);
    WordFormsBuilder builder = new WordFormsBuilder(language);

    String result = getForm(builder.computeForms("ÉÑÀÇÏÔ"), NO_ACCENTS);

    assertThat(result, equalTo("enacio"));
  }

  @Test
  public void shouldUseLocaleForLowerCase() {
    Language language = new Language("tr", LATIN);
    WordFormsBuilder builder = new WordFormsBuilder(language);

    String result = getForm(builder.computeForms("PRINÇE"), LOWERCASE);

    assertThat(result, equalTo("prınçe"));
  }

  @Test
  public void shouldComputeWordOnlyForm() {
    Language language = new Language("cs", LATIN).setAdditionalConsonants("č",
        "ř");
    WordFormsBuilder builder = new WordFormsBuilder(language);

    String result = getForm(builder.computeForms("ČL-OV'ěk-ůŘ"),
        NO_ACCENTS_WORD_CHARS_ONLY);

    assertThat(result, equalTo("človekuř"));
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowForEmptyWord() {
    Language language = new Language("nl", LATIN);
    WordFormsBuilder builder = new WordFormsBuilder(language);
    builder.computeForms("");
  }

  private String getForm(String[] forms, WordForm wordForm) {
    return forms[wordForm.ordinal()];
  }

}
