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
 * Test for {@link Palindromes}.
 */
class PalindromesTest extends AbstractEvaluatorTest {

  private final Palindromes palindromes = new Palindromes();

  @Test
  void shouldRecognizePalindromes() {
    // given
    // otto, bagab, -, awkwa, bab/ili, bab, -
    List<Word> words = createWords("trottoir", "ebagabo", "palindrome", "awkward",
        "probability", "probable", "o");

    // when
    palindromes.evaluate(words);

    // then
    Map<String, Set<String>> results = groupByKey(palindromes.getResults());
    assertThat(results, aMapWithSize(5));
    assertThat(results.get("bab"), containsInAnyOrder("probability", "probable"));
    assertThat(results.get("ili"), contains("probability"));
    assertThat(results.get("otto"), contains("trottoir"));
    assertThat(results.get("awkwa"), contains("awkward"));
    assertThat(results.get("bagab"), contains("ebagabo"));
  }

  @Test
  void shouldNotAddSimplePairs() {
    // given
    List<Word> words = createWords("gaaf", "aardvaark", "letter", "boot", "bleed");

    // when
    palindromes.evaluate(words);

    // then
    Map<String, Set<String>> results = groupByKey(palindromes.getResults());
    assertThat(results, aMapWithSize(1));
    assertThat(results.get("ette"), contains("letter"));
  }
}
