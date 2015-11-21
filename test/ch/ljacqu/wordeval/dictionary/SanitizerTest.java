package ch.ljacqu.wordeval.dictionary;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

public class SanitizerTest {
  
  private static Sanitizer sanitizer;
  
  @BeforeClass
  public static void setUpSettings() {
    DictionarySettings settings = new DictionarySettings("zxx")
        .setDelimiters('/', '#')
        .setSkipSequences("aaa", "è");
    sanitizer = new Sanitizer(settings);
  }

  @Test
  public void shouldRemoveDelimiters() {
    String[] lines = { "tëst/23", "abcdef#abc", "Vutsr/abc#ef", "emptyWord" };

    String[] results = new String[lines.length];
    for (int i = 0; i < lines.length; ++i) {
      results[i] = sanitizer.isolateWord(lines[i]);
    }

    String[] expected = { "tëst", "abcdef", "Vutsr", "emptyWord" };
    assertThat(results, arrayContaining(expected));
  }

  @Test
  public void shouldSkipWordsWithSkipSequence() {
    String[] words = { "test", "tèst", "abcaaa", "abcdefg", "abcdefgè" };
    boolean[] shouldBeSkipped = { false, true, true, false, true };

    for (int i = 0; i < words.length; ++i) {
      String result = sanitizer.isolateWord(words[i]);
      if (shouldBeSkipped[i]) {
        assertThat(result, emptyString());
      } else {
        assertThat(result, not(emptyString()));
      }
    }
  }
  
  @Test
  public void shouldSkipWordsWithNumbers() {
    String[] words = { "9th", "asdf1", "3.1415926", "to4st" };
    
    Arrays.stream(words)
      .forEach(word -> assertThat(sanitizer.isolateWord(word), emptyString()));
  }

}
