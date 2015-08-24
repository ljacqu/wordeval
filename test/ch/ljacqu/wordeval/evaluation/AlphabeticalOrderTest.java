package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class AlphabeticalOrderTest {
  private AlphabeticalOrder evaluator;

  @Before
  public void initializeEvaluator() {
    evaluator = new AlphabeticalOrder();
  }

  @Test
  public void shouldRecognizeWordsWithAlphabeticalOrder() {
    // 4, 0, 5, 0, 4, 4, 5, 0, 8
    String[] words = { "acer", "paper", "zymga", "jigsaw", "pong", "mopr",
        "bruxz", "contact", "ahpqtvwx" };

    for (String word : words) {
      evaluator.processWord(word, word);
    }
    Map<Integer, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 3);
    assertEquals(results.get(4).size(), 3);
    assertEquals(results.get(5).size(), 2);
    assertEquals(results.get(8).size(), 1);

    assertEquals(results.get(4).get(2), "mopr");
    assertEquals(results.get(5).get(0), "zymga");
    assertEquals(results.get(8).get(0), "ahpqtvwx");
  }

}
