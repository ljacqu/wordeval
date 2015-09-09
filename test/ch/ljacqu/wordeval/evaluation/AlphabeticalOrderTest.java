package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
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
    String[] words = { "acer", "paper", "bruxz", "jigsaw", "mopr", "pong",
        "zymga", "contact", "ahpqtvwx" };

    for (String word : words) {
      evaluator.processWord(word, word);
    }
    Map<Integer, List<String>> results = evaluator.getNavigableResults();

    assertThat(results.size(), equalTo(3));
    assertThat(results.get(4).size(), equalTo(3));
    assertThat(results.get(5).size(), equalTo(2));
    assertThat(results.get(8).size(), equalTo(1));

    assertThat(results.get(4).get(1), equalTo("mopr"));
    assertThat(results.get(5).get(0), equalTo("bruxz"));
    assertThat(results.get(5).get(1), equalTo("zymga"));
    assertThat(results.get(8).get(0), equalTo("ahpqtvwx"));
  }

}
