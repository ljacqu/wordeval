package ch.ljacqu.wordeval.language;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class WordFormsGeneratorTest {

  @Test
  public void shouldKeepAdditionalLetters() {
    Language language = new Language("da").setAdditionalVowels("æ", "ø", "å");
    WordFormsBuilder generator = new WordFormsBuilder(language);
    String[] words = { "forsøgte erklære trådte", "Å ǿ én býr" };

    List<String> result = new ArrayList<String>();
    for (String word : words) {
      String[] wordForms = generator.computeForms(word);
      result.add(getNoAccent(wordForms));
    }

    String[] expected = { "forsøgte erklære trådte", "å ø en byr" };
    assertArrayEquals(result.toArray(), expected);
  }

  @Test
  public void shouldRemoveAllAccentsByDefault() {
    Language language = new Language("fr");
    WordFormsBuilder generator = new WordFormsBuilder(language);

    String result = getNoAccent(generator.computeForms("ÉÑÀÇÏÔ"));

    assertEquals(result, "enacio");
  }

  private String getNoAccent(String[] wordForms) {
    return wordForms[WordForm.NO_ACCENTS.ordinal()];
  }

}
