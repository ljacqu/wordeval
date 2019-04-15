package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link SameLetterConsecutive}.
 */
public class SameLetterConsecutiveTest {

  private SameLetterConsecutive evaluator = new SameLetterConsecutive();

  @Test
  public void shouldRecognizeConsecutiveLetters() {
    // given
    // ll, fff, eee, -, fff, ll, ll
    String[] words = { "hello", "schifffahrt", "geeet", "window", "töfffahrer", "schnell", "llama" };

    // when
    Map<String, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByKey(evaluator, words);

    // then
    assertThat(results, aMapWithSize(3));
    assertThat(results.get("ll"), containsInAnyOrder("hello", "schnell", "llama"));
    assertThat(results.get("fff"), containsInAnyOrder("schifffahrt", "töfffahrer"));
    assertThat(results.get("eee"), containsInAnyOrder("geeet"));
  }

  @Test
  public void shouldRecognizeSeparateOccurrences() {
    // given
    // {sss,bb}, {ss,pp}, {aa,ss}, {ooo,ee,oo}
    String[] words = { "Massstabbrecher", "Reisstopp", "aabesso", "oooeemoo" };

    // when
    Map<String, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByKey(evaluator, words);

    // then
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
  public void shouldProcessCyrillicWords() {
    // given
    // нн, -, дд, нн, -
    String[] words = { "избранные", "величайший", "поддержки", "старинного", "независимая" };

    // when
    Map<String, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByKey(evaluator, words);

    // then
    assertThat(results, aMapWithSize(2));
    assertThat(results.get("дд"), contains("поддержки"));
    assertThat(results.get("нн"), containsInAnyOrder("избранные", "старинного"));
  }
}
