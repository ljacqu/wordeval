package ch.jalu.wordeval.evaluation;

import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import static ch.jalu.wordeval.TestUtil.processWords;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for {@link AlphabeticalOrder}.
 */
class AlphabeticalOrderTest {

  @Test
  void shouldRecognizeWordsWithAlphabeticalOrder() {
    // given
    // 4, 0, 5, 0, 4, 4, 5, 0, 8, 4
    String[] words = { "acer", "paper", "bruxz", "jigsaw", "mopr", "pong",
        "zymga", "contact", "ahpqtvwx", "beer" };
    AlphabeticalOrder evaluator = new AlphabeticalOrder();

    // when
    processWords(evaluator, words);
    Multimap<Integer, String> results = evaluator.getResults();

    // then
    assertThat(results.keySet(), hasSize(3));
    assertThat(results.get(4), containsInAnyOrder("acer", "mopr", "pong", "beer"));
    assertThat(results.get(5), containsInAnyOrder("bruxz", "zymga"));
    assertThat(results.get(8), contains("ahpqtvwx"));
  }

}
