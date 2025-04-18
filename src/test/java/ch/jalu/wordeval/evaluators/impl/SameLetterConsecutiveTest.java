package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link SameLetterConsecutive}.
 */
class SameLetterConsecutiveTest extends AbstractEvaluatorTest {

  private final SameLetterConsecutive sameLetterConsecutive = new SameLetterConsecutive();

  @Test
  void shouldRecognizeConsecutiveLetters() {
    // given
    // ll, fff, eee, -, fff, ll, ll
    List<Word> words = createWords("hello", "schifffahrt", "geeet", "window", "töfffahrer", "schnell", "llama");

    // when
    sameLetterConsecutive.evaluate(words);

    // then
    Map<String, Set<String>> results = groupByKey(sameLetterConsecutive.getResults());
    assertThat(results, aMapWithSize(3));
    assertThat(results.get("ll"), containsInAnyOrder("hello", "schnell", "llama"));
    assertThat(results.get("fff"), containsInAnyOrder("schifffahrt", "töfffahrer"));
    assertThat(results.get("eee"), containsInAnyOrder("geeet"));
  }

  @Test
  void shouldRecognizeSeparateOccurrences() {
    // given
    // {sss,bb}, {ss,pp}, {aa,ss}, {ooo,ee,oo}
    List<Word> words = createWords("Massstabbrecher", "Reisstopp", "aabesso", "oooeemoo");

    // when
    sameLetterConsecutive.evaluate(words);

    // then
    Map<String, Set<String>> results = groupByKey(sameLetterConsecutive.getResults());
    assertThat(results, aMapWithSize(8));
    assertThat(results.get("aa"), containsInAnyOrder("aabesso"));
    assertThat(results.get("bb"), containsInAnyOrder("Massstabbrecher"));
    assertThat(results.get("ee"), containsInAnyOrder("oooeemoo"));
    assertThat(results.get("oo"), containsInAnyOrder("oooeemoo"));
    assertThat(results.get("pp"), containsInAnyOrder("Reisstopp"));
    assertThat(results.get("ss"), containsInAnyOrder("Reisstopp", "aabesso"));
    assertThat(results.get("ooo"), containsInAnyOrder("oooeemoo"));
    assertThat(results.get("sss"), containsInAnyOrder("Massstabbrecher"));
  }

  @Test
  void shouldProcessCyrillicWords() {
    // given
    // нн, -, дд, нн, -
    List<Word> words = createWords("избранные", "величайший", "поддержки", "старинного", "независимая");

    // when
    sameLetterConsecutive.evaluate(words);

    // then
    Map<String, Set<String>> results = groupByKey(sameLetterConsecutive.getResults());
    assertThat(results, aMapWithSize(2));
    assertThat(results.get("дд"), contains("поддержки"));
    assertThat(results.get("нн"), containsInAnyOrder("избранные", "старинного"));
  }
}
