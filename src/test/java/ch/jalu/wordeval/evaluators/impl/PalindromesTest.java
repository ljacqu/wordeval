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
 * Test for {@link Palindromes}.
 */
public class PalindromesTest {

  private Palindromes evaluator = new Palindromes();

  @Test
  public void shouldRecognizePalindromes() {
    // given
    // otto, bagab, -, awkwa, bab/ili, bab, -
    String[] words = { "trottoir", "ebagabo", "palindrome", "awkward",
        "probability", "probable", "o" };

    // when
    Map<String, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByKey(evaluator, words);

    // then
    assertThat(results, aMapWithSize(5));
    assertThat(results.get("bab"), containsInAnyOrder("probability", "probable"));
    assertThat(results.get("ili"), contains("probability"));
    assertThat(results.get("otto"), contains("trottoir"));
    assertThat(results.get("awkwa"), contains("awkward"));
    assertThat(results.get("bagab"), contains("ebagabo"));
  }

  @Test
  public void shouldNotAddSimplePairs() {
    // given
    String[] words = { "gaaf", "aardvaark", "letter", "boot", "bleed" };

    // when
    Map<String, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByKey(evaluator, words);

    // then
    assertThat(results, aMapWithSize(1));
    assertThat(results.get("ette"), contains("letter"));
  }
}
