package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link AlphabeticalOrder}.
 */
class AlphabeticalOrderTest extends AbstractEvaluatorTest {

  private final AlphabeticalOrder alphabeticalOrder = new AlphabeticalOrder();

  @Test
  void shouldRecognizeWordsWithAlphabeticalOrder() {
    // given
    // 4, 0, 5, 0, 4, 4, 5, 0, 8, 4
    List<Word> words = createWords("acer", "paper", "bruxz", "jigsaw", "mopr", "pong",
        "zymga", "contact", "ahpqtvwx", "beer");

    // when
    alphabeticalOrder.evaluate(words);

    // then
    Map<Double, Set<String>> results = groupByScore(alphabeticalOrder.getResults());
    assertThat(results, aMapWithSize(3));
    assertThat(results.get(4.0), containsInAnyOrder("acer", "mopr", "pong", "beer"));
    assertThat(results.get(5.0), containsInAnyOrder("bruxz", "zymga"));
    assertThat(results.get(8.0), contains("ahpqtvwx"));
  }

}
