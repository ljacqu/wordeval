package ch.jalu.wordeval.evaluation;

import com.google.common.collect.Multimap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.jalu.wordeval.TestUtil.processWords;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for {@link Palindromes}.
 */
class PalindromesTest {

  private Palindromes evaluator;

  @BeforeEach
  void initializeEvaluator() {
    evaluator = new Palindromes();
  }

  @Test
  void shouldRecognizePalindromes() {
    // otto, bagab, -, awkwa, bab/ili, bab, -
    String[] words = { "trottoir", "ebagabo", "palindrome", "awkward",
        "probability", "probable", "o" };

    processWords(evaluator, words);
    Multimap<String, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(5));
    assertThat(results.get("bab"), containsInAnyOrder("probability", "probable"));
    assertThat(results.get("ili"), contains("probability"));
    assertThat(results.get("otto"), contains("trottoir"));
    assertThat(results.get("awkwa"), contains("awkward"));
    assertThat(results.get("bagab"), contains("ebagabo"));
  }

  @Test
  void shouldNotAddSimplePairs() {
    String[] words = { "gaaf", "aardvaark", "letter", "boot", "bleed" };

    processWords(evaluator, words);
    Multimap<String, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(1));
    assertThat(results.get("ette"), contains("letter"));
  }

}
