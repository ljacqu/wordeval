package ch.ljacqu.wordeval.dictionary;

import static ch.ljacqu.wordeval.dictionary.WordForm.LOWERCASE;
import static ch.ljacqu.wordeval.dictionary.WordForm.NO_ACCENTS;
import static ch.ljacqu.wordeval.dictionary.WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.dictionary.WordFormsBuilder;
import ch.ljacqu.wordeval.language.Language;

public class WordFormsBuilderTest {

  @Test
  public void shouldKeepAdditionalLetters() {
    Language language = new Language("da").setAdditionalVowels("æ", "ø", "å");
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
    Language language = new Language("fr");
    WordFormsBuilder builder = new WordFormsBuilder(language);

    String result = getForm(builder.computeForms("ÉÑÀÇÏÔ"), NO_ACCENTS);

    assertThat(result, equalTo("enacio"));
  }

  @Test
  public void shouldUseLocaleForLowerCase() {
    Language language = new Language("tr");
    WordFormsBuilder builder = new WordFormsBuilder(language);

    String result = getForm(builder.computeForms("PRINÇE"), LOWERCASE);

    assertThat(result, equalTo("prınçe"));
  }

  @Test
  public void shouldComputeWordOnlyForm() {
    Language language = new Language("cs").setAdditionalConsonants("č", "ř");
    WordFormsBuilder builder = new WordFormsBuilder(language);

    String result = getForm(builder.computeForms("ČL-OV'ěk-ůŘ"),
        NO_ACCENTS_WORD_CHARS_ONLY);

    assertThat(result, equalTo("človekuř"));
  }

  @Test
  public void shouldReturnEmptyArrayForEmptyWord() {
    Language language = new Language("nl");
    WordFormsBuilder builder = new WordFormsBuilder(language);
    String[] result = builder.computeForms("");

    assertThat(result, emptyArray());
  }

  private String getForm(String[] forms, WordForm wordForm) {
    return forms[wordForm.ordinal()];
  }

}
