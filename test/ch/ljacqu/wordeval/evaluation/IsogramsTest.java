package ch.ljacqu.wordeval.evaluation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import java.util.Map;
import java.util.Set;
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
    Map<Integer, Set<String>> results = evaluator.getResults();

    assertThat(results, aMapWithSize(2));
    assertThat(results.get(6), contains("jigsaw"));
    assertThat(results.get(14), contains("halfduimspyker"));
  }

}
