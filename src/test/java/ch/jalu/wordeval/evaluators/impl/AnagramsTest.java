package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link Anagrams}.
 */
class AnagramsTest {

  private Anagrams evaluator = new Anagrams();

  @Test
  void shouldFindAnagrams() {
    // given
    // {race, care, acre}, {tea, eat}, {fro, for}, a, something, test
    String[] words = { "race", "for", "a", "eat", "care", "something", "acre", "fro", "tea", "test", "test" };

    // when
    Map<String, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupWordsByKey(evaluator, words);

    // then
    assertThat(results, aMapWithSize(3));
    assertThat(results.get("acer"), containsInAnyOrder("race", "care", "acre"));
    assertThat(results.get("aet"), containsInAnyOrder("tea", "eat"));
    assertThat(results.get("for"), containsInAnyOrder("for", "fro"));
  }
}
