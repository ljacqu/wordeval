package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class IsogramsTest {

  private Isograms evaluator;

  @Before
  public void initializeEvaluator() {
    evaluator = new Isograms();
  }

  @Test
  public void shouldRecognizeIsograms() {
    String[] words = { "halfduimspyker", "abcdefga", "abcdefgcijk", "jigsaw" };

    for (String word : words) {
      evaluator.processWord(word, word);
    }
    Map<Integer, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 2);
    assertEquals(results.get(6).size(), 1);
    assertEquals(results.get(14).size(), 1);
    assertEquals(results.get(14).get(0), "halfduimspyker");
  }

}
