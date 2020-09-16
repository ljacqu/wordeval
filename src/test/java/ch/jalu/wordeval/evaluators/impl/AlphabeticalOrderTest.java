package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link AlphabeticalOrder}.
 */
class AlphabeticalOrderTest {

  private AlphabeticalOrder evaluator = new AlphabeticalOrder();

  @Test
  void shouldRecognizeWordsWithAlphabeticalOrder() {
    // given
    // 4, 0, 5, 0, 4, 4, 5, 0, 8, 4
    String[] words = { "acer", "paper", "bruxz", "jigsaw", "mopr", "pong",
        "zymga", "contact", "ahpqtvwx", "beer" };

    // when
    Map<Double, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByScore(evaluator, words);

    // then
    assertThat(results, aMapWithSize(3));
    assertThat(results.get(4.0), containsInAnyOrder("acer", "mopr", "pong", "beer"));
    assertThat(results.get(5.0), containsInAnyOrder("bruxz", "zymga"));
    assertThat(results.get(8.0), contains("ahpqtvwx"));
  }

}
