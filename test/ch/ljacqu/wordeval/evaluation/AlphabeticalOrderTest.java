package ch.ljacqu.wordeval.evaluation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import java.util.Map;
import java.util.Set;
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
    // 4, 0, 5, 0, 4, 4, 5, 0, 8, 4
    String[] words = { "acer", "paper", "bruxz", "jigsaw", "mopr", "pong",
        "zymga", "contact", "ahpqtvwx", "beer" };

    for (String word : words) {
      evaluator.processWord(word, word);
    }
    Map<Integer, Set<String>> results = evaluator.getResults();

    assertThat(results, aMapWithSize(3));
    assertThat(results.get(4), containsInAnyOrder("acer", "mopr", "pong", "beer"));
    assertThat(results.get(5), containsInAnyOrder("bruxz", "zymga"));
    assertThat(results.get(8), contains("ahpqtvwx"));
  }

}
