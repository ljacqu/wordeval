package ch.ljacqu.wordeval.evaluation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class ConsecutiveLetterPairsTest {

  private ConsecutiveLetterPairs evaluator;

  @Before
  public void initializeEvaluator() {
    evaluator = new ConsecutiveLetterPairs();
  }

  private void processWords(String[] words) {
    for (String word : words) {
      evaluator.processWord(word, word);
    }
  }

  @Test
  public void shouldRecognizeLetterPairs() {
    // 2, 0, 0, 3, 2, 2, 2, 4
    String[] words = { "aallorr", "potato", "klokken", "maaiill", "oppaan",
        "reennag", "baaggage", "voorraaddra" };

    processWords(words);
    Map<Integer, Set<String>> results = evaluator.getResults();

    assertThat(results, aMapWithSize(3));
    assertThat(results.get(2),
        containsInAnyOrder("aallorr", "oppaan", "reennag", "baaggage"));
    assertThat(results.get(3), contains("maaiill"));
    assertThat(results.get(4), contains("voorraaddra"));
  }

  @Test
  public void shouldRecognizeSeparatePairs() {
    // 2, {2,3}, 0
    String[] words = { "massaage", "aabbcdefgghhiij", "something" };

    processWords(words);
    Map<Integer, Set<String>> results = evaluator.getResults();

    assertThat(results, aMapWithSize(2));
    assertThat(results.get(2),
        containsInAnyOrder("massaage", "aabbcdefgghhiij"));
    assertThat(results.get(3), containsInAnyOrder("aabbcdefgghhiij"));
  }

  @Test
  /**
   * Triplets ("eee") or bigger groups should also count towards "pair groups",
   * i.e. "sseee" should count as 2 groups.
   */
  public void shouldRecognizeTriplesOrMore() {
    // 2, 0, 3, 4, 0
    String[] words = { "laaaii", "kayak", "poolooeeerr", "aabbbccdddef",
        "walking" };

    processWords(words);
    Map<Integer, Set<String>> results = evaluator.getResults();

    assertThat(results, aMapWithSize(3));
    assertThat(results.get(2), contains("laaaii"));
    assertThat(results.get(3), contains("poolooeeerr"));
    assertThat(results.get(4), contains("aabbbccdddef"));
  }

}
