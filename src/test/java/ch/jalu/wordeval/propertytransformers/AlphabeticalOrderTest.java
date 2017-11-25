package ch.jalu.wordeval.propertytransformers;

import ch.jalu.wordeval.dictionary.TestWord;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link AlphabeticalOrder}.
 */
public class AlphabeticalOrderTest {

  @Test
  public void shouldRecognizeWordsWithAlphabeticalOrder() {
    // given
    // 4, 0, 5, 0, 4, 4, 5, 0, 8, 4
    Map<String, Integer> testCases = ImmutableMap.<String, Integer>builder()
        .put("acer", 4)
        .put("paper", 0)
        .put("bruxz", 5)
        .put("jigsaw", 0)
        .put("mopr", 4)
        .put("pong", 4)
        .put("zymga", 5)
        .put("contact", 0)
        .put("ahpqtvwx", 8)
        .put("beer", 4)
        .build();

    AlphabeticalOrder evaluator = new AlphabeticalOrder();

    // when / then
    for (Map.Entry<String, Integer> testCase : testCases.entrySet()) {
      List<Integer> result = evaluator.findProperties(new TestWord(testCase.getKey()));
      if (testCase.getValue() == 0) {
        assertThat("for " + testCase.getKey(), result, empty());
      } else {
        assertThat("for " + testCase.getKey(), result, hasSize(1));
        assertThat("for " + testCase.getKey(), result.get(0), equalTo(testCase.getValue()));
      }
    }
  }
}