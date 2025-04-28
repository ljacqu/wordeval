package ch.jalu.wordeval.dictionary.hunspell.sanitizer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link HunspellSanitizer}.
 */
class HunspellSanitizerTest {
  
  private static HunspellSanitizer sanitizer;
  
  @BeforeAll
  static void setUpSettings() {
    sanitizer = new HunspellSanitizer("aaa", "è");
  }

  @Test
  void shouldSkipWordsWithSkipSequence() {
    // given
    String[] words = { "test", "tèst", "abcaaa", "abcdefg", "abcdefgè" };
    boolean[] shouldBeSkipped = { false, true, true, false, true };

    for (int i = 0; i < words.length; ++i) {
      // when
      boolean result = sanitizer.skipLine(words[i]);

      // then
      assertThat(result, equalTo(shouldBeSkipped[i]));
    }
  }

  @Test
  void shouldReturnWordByDefault() {
    // given / when
    assertThat(sanitizer.transform("beets"), equalTo("beets"));
    assertThat(sanitizer.transform(""), equalTo(""));
    assertThat(sanitizer.transform("Æ"), equalTo("Æ"));
  }
}
