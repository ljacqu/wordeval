package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.language.Language;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link Sanitizer}.
 */
class SanitizerTest {
  
  private static Sanitizer sanitizer;
  
  @BeforeAll
  static void setUpSettings() {
    Language language = mock(Language.class);
    Dictionary settings = Dictionary.builder()
        .identifier("zxx")
        .file("bogus")
        .language(language)
        .delimiters('/', '#')
        .skipSequences("aaa", "è")
        .build();
    sanitizer = new Sanitizer(settings);
  }

  @Test
  void shouldRemoveDelimiters() {
    // given
    String[] lines = { "tëst/23", "abcdef#abc", "Vutsr/abc#ef", "emptyWord" };

    String[] results = new String[lines.length];
    for (int i = 0; i < lines.length; ++i) {
      // when
      results[i] = sanitizer.isolateWord(lines[i]);
    }

    // then
    String[] expected = { "tëst", "abcdef", "Vutsr", "emptyWord" };
    assertThat(results, arrayContaining(expected));
  }

  @Test
  void shouldSkipWordsWithSkipSequence() {
    // given
    String[] words = { "test", "tèst", "abcaaa", "abcdefg", "abcdefgè" };
    boolean[] shouldBeSkipped = { false, true, true, false, true };

    for (int i = 0; i < words.length; ++i) {
      // when / then
      String result = sanitizer.isolateWord(words[i]);
      if (shouldBeSkipped[i]) {
        assertThat(result, nullValue());
      } else {
        assertThat(result, not(emptyOrNullString()));
      }
    }
  }
  
  @Test
  void shouldSkipWordsWithNumbers() {
    // given
    String[] words = { "9th", "asdf1", "3.1415926", "to4st" };

    // when / then
    Arrays.stream(words)
      .forEach(word -> assertThat(sanitizer.isolateWord(word), nullValue()));
  }

  @Test
  void shouldSkipRomanNumerals() {
    // given
    List<String> words = List.of("VII", "XXV", "MCMXII");

    // when / then
    words.forEach(word -> assertThat(sanitizer.isolateWord(word), nullValue()));
  }

  @Test
  void shouldNotSkipWordsWithNumberAfterDelimiter() {
    // given / when
    String word = sanitizer.isolateWord("sand/P4");

    // then
    assertThat(word, equalTo("sand"));
  }
}
