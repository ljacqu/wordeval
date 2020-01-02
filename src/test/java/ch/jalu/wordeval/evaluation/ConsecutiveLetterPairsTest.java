package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.TestUtil;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for {@link ConsecutiveLetterPairs}.
 */
class ConsecutiveLetterPairsTest {

  private ConsecutiveLetterPairs evaluator;

  @BeforeEach
  void initializeEvaluator() {
    evaluator = new ConsecutiveLetterPairs();
  }

  @Test
  void shouldRecognizeLetterPairs() {
    // 2, 0, 0, 3, 2, 2, 2, 4, 2
    String[] words = { "aallorr", "potato", "klokken", "maaiill", "oppaan",
        "reennag", "baaggage", "voorraaddra", "reell" };

    TestUtil.processWords(evaluator, words);
    Multimap<Integer, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(3));
    assertThat(results.get(2), containsInAnyOrder("aallorr", "oppaan", "reennag", "baaggage", "reell"));
    assertThat(results.get(3), contains("maaiill"));
    assertThat(results.get(4), contains("voorraaddra"));
  }

  @Test
  void shouldRecognizeSeparatePairs() {
    // 2, {2,3}, 0
    String[] words = { "massaage", "aabbcdefgghhiij", "something" };

    TestUtil.processWords(evaluator, words);
    Multimap<Integer, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(2));
    assertThat(results.get(2), containsInAnyOrder("massaage", "aabbcdefgghhiij"));
    assertThat(results.get(3), containsInAnyOrder("aabbcdefgghhiij"));
  }


  /**
   * Triplets ("eee") or bigger groups should also count towards "pair groups",
   * i.e. "sseee" should count as 2 groups.
   */
  @Test
  void shouldRecognizeTriplesOrMore() {
    // 2, 0, 3, 4, 0
    String[] words = { "laaaii", "kayak", "poolooeeerr", "aabbbccdddef",
        "walking" };

    TestUtil.processWords(evaluator, words);
    Multimap<Integer, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(3));
    assertThat(results.get(2), contains("laaaii"));
    assertThat(results.get(3), contains("poolooeeerr"));
    assertThat(results.get(4), contains("aabbbccdddef"));
  }

}
