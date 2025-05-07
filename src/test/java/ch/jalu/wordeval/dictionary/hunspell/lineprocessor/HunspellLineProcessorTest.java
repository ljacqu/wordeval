package ch.jalu.wordeval.dictionary.hunspell.lineprocessor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link HunspellLineProcessor}.
 */
class HunspellLineProcessorTest {
  
  private static HunspellLineProcessor lineProcessor;
  
  @BeforeAll
  static void setUpSettings() {
    lineProcessor = new HunspellLineProcessor("aaa", "è");
  }

  @Test
  void shouldSplitWords() {
    // given / when
    RootAndAffixes testAb  = lineProcessor.split("test/AB");
    RootAndAffixes toast   = lineProcessor.split("toast");
    RootAndAffixes tasteBc = lineProcessor.split("taste/BC [ph:tejst]");

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
      RootAndAffixes result = lineProcessor.split(words[i]);

      // then
      assertThat(result.isEmpty(), equalTo(shouldBeSkipped[i]));
      assertThat(result.affixFlags(), notNullValue());
    }
  }

  @Test
  void shouldReturnWordByDefault() {
    // given / when
    assertThat(lineProcessor.transform("beets"), equalTo("beets"));
    assertThat(lineProcessor.transform(""), equalTo(""));
    assertThat(lineProcessor.transform("Æ"), equalTo("Æ"));
  }

  /* Slashes can be escaped in Hunspell, but there's no point in supporting this in this project. */
  @Test
  void shouldThrowForWordWithBackslash() {
    // given / when
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> lineProcessor.split("km\\/h"));

    // then
    assertThat(ex.getMessage(), equalTo("Backslash found in line: km\\/h"));
  }

  @Test
  void shouldNotSkipWordIfAffixClassHasSkipSequence() {
    // given
    String[] words = { "test/Pè", "toast/D tèst" };

    // when
    List<RootAndAffixes> rootsAndAffixes = Arrays.stream(words)
        .map(word -> lineProcessor.split(word))
        .toList();

    // then
    assertThat(rootsAndAffixes, hasSize(2));
    assertThat(rootsAndAffixes.get(0), equalTo(new RootAndAffixes("test", "Pè")));
    assertThat(rootsAndAffixes.get(1), equalTo(new RootAndAffixes("toast", "D")));
  }
}
