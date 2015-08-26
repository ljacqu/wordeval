package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
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
    // 2, 0, 0, 2, 3, 2, 2, 4
    String[] words = { "reennag", "potato", "klokken", "oppaan", "maaiill",
        "aallorr", "baaggage", "voorraaddra" };

    processWords(words);
    Map<Integer, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 3);
    assertEquals(results.get(2).size(), 4);
    assertEquals(results.get(3).size(), 1);
    assertEquals(results.get(4).size(), 1);

    assertEquals(results.get(2).get(2), "aallorr");
    assertEquals(results.get(3).get(0), "maaiill");
  }

  @Test
  public void shouldRecognizeSeparatePairs() {
    // 2, {2,3}, 0
    String[] words = { "massaage", "aabbcdefgghhiij", "something" };

    processWords(words);
    Map<Integer, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 2);
    assertEquals(results.get(2).size(), 2);
    assertEquals(results.get(3).size(), 1);

    assertEquals(results.get(3).get(0), results.get(2).get(1));
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
    Map<Integer, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 3);
    assertEquals(results.get(2).size(), 1);
    assertEquals(results.get(3).size(), 1);
    assertEquals(results.get(4).size(), 1);

    assertEquals(results.get(2).get(0), "laaaii");
    assertEquals(results.get(4).get(0), "aabbbccdddef");
  }

}
