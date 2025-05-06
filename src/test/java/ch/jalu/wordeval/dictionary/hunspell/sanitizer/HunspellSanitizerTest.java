package ch.jalu.wordeval.dictionary.hunspell.sanitizer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  void shouldSplitWords() {
    // given / when
    RootAndAffixes testAb  = sanitizer.split("test/AB");
    RootAndAffixes toast   = sanitizer.split("toast");
    RootAndAffixes tasteBc = sanitizer.split("taste/BC [ph:tejst]");

    // then
    assertThat(testAb,  equalTo(new RootAndAffixes("test", "AB")));
    assertThat(toast,   equalTo(new RootAndAffixes("toast", "")));
    assertThat(tasteBc, equalTo(new RootAndAffixes("taste", "BC")));
  }

  @Test
  void shouldSplitWordsWithSkipSequenceToEmptyObj() {
    // given
    String[] words = { "test", "tèst", "abcaaa/PD", "abcdefg/PD", "abcdefgè/TE" };
    boolean[] shouldBeSkipped = { false, true, true, false, true };

    for (int i = 0; i < words.length; ++i) {
      // when
      RootAndAffixes result = sanitizer.split(words[i]);

      // then
      if (shouldBeSkipped[i]) {
        assertThat(result.root(), emptyString());
      } else {
        assertThat(result.root().isEmpty(), equalTo(false));
      }
      assertThat(result.affixFlags(), notNullValue());
    }
  }

  @Test
  void shouldReturnWordByDefault() {
    // given / when
    assertThat(sanitizer.transform("beets"), equalTo("beets"));
    assertThat(sanitizer.transform(""), equalTo(""));
    assertThat(sanitizer.transform("Æ"), equalTo("Æ"));
  }

  /* Slashes can be escaped in Hunspell, but there's no point in supporting this in this project. */
  @Test
  void shouldThrowForWordWithBackslash() {
    // given / when
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> sanitizer.split("km\\/h"));

    // then
    assertThat(ex.getMessage(), equalTo("Backslash found in line: km\\/h"));
  }
}
