package ch.jalu.wordeval.evaluation;

import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import static ch.jalu.wordeval.TestUtil.processWords;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Palindromes}.
 */
public class PalindromesTest {

  private Palindromes evaluator;

  @Before
  public void initializeEvaluator() {
    evaluator = new Palindromes();
  }

  @Test
  public void shouldRecognizePalindromes() {
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
  public void shouldNotAddSimplePairs() {
    String[] words = { "gaaf", "aardvaark", "letter", "boot", "bleed" };

    processWords(evaluator, words);
    Multimap<String, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(1));
    assertThat(results.get("ette"), contains("letter"));
  }

}
