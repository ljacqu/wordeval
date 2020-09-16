package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for {@link ConsecutiveLetterPairs}.
 */
class ConsecutiveLetterPairsTest {

  private ConsecutiveLetterPairs evaluator = new ConsecutiveLetterPairs();

  @Test
  void shouldRecognizeLetterPairs() {
    // given
    // 2, 0, 0, 3, 2, 2, 2, 4, 2
    String[] words = { "aallorr", "potato", "klokken", "maaiill", "oppaan",
        "reennag", "baaggage", "voorraaddra", "reell" };

    // when
    Map<Double, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByScore(evaluator, words);

    // then
    assertThat(results.keySet(), hasSize(3));
    assertThat(results.get(2.0), containsInAnyOrder("aallorr", "oppaan", "reennag", "baaggage", "reell"));
    assertThat(results.get(3.0), contains("maaiill"));
    assertThat(results.get(4.0), contains("voorraaddra"));
  }

  @Test
  void shouldRecognizeSeparatePairs() {
    // given
    // 2, {2,3}, 0
    String[] words = { "massaage", "aabbcdefgghhiij", "something" };

    // when
    Map<Double, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByScore(evaluator, words);

    // then
    assertThat(results.keySet(), hasSize(2));
    assertThat(results.get(2.0), containsInAnyOrder("massaage", "aabbcdefgghhiij"));
    assertThat(results.get(3.0), containsInAnyOrder("aabbcdefgghhiij"));
  }

  /**
   * Triplets ("eee") or bigger groups should also count towards "pair groups",
   * i.e. "sseee" should count as 2 groups.
   */
  @Test
  void shouldRecognizeTriplesOrMore() {
    // given
    // 2, 0, 3, 4, 0
    String[] words = { "laaaii", "kayak", "poolooeeerr", "aabbbccdddef",
        "walking" };

    // when
    Map<Double, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByScore(evaluator, words);

    // then
    assertThat(results.keySet(), hasSize(3));
    assertThat(results.get(2.0), contains("laaaii"));
    assertThat(results.get(3.0), contains("poolooeeerr"));
    assertThat(results.get(4.0), contains("aabbbccdddef"));
  }

}
