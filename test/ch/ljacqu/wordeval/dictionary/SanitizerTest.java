package ch.ljacqu.wordeval.dictionary;

import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import ch.ljacqu.wordeval.language.Language;

public class SanitizerTest {

  @Test
  public void shouldRemoveDelimiters() {
    DictionarySettings settings = new DictionarySettings("zxx").setDelimiters('/', '#');
    Sanitizer sanitizer = new Sanitizer(new Language("zxx", LATIN), settings);
    String[] lines = { "tëst/23", "abcdef#abc", "Vutsr/abc#ef", "emptyWord" };

    String[] results = new String[lines.length];
    for (int i = 0; i < lines.length; ++i) {
      String[] sanitizedForms = sanitizer.computeForms(lines[i]);
      results[i] = sanitizedForms[0];
    }

    String[] expected = { "tëst", "abcdef", "Vutsr", "emptyWord" };
    assertThat(results, arrayContaining(expected));
  }

  @Test
  public void shouldSkipWordsWithSkipSequence() {
    DictionarySettings settings = new DictionarySettings("zxx")
        .setSkipSequences("aaa", "è");
    Sanitizer sanitizer = new Sanitizer(new Language("zxx", LATIN), settings);
    String[] words = { "test", "tèst", "abcaaa", "abcdefg", "abcdefgè" };
    boolean[] shouldBeSkipped = { false, true, true, false, true };

    for (int i = 0; i < words.length; ++i) {
      String[] result = sanitizer.computeForms(words[i]);
      if (shouldBeSkipped[i]) {
        assertThat(result, emptyArray());
      } else {
        assertThat(result, not(emptyArray()));
      }
    }
  }

}
