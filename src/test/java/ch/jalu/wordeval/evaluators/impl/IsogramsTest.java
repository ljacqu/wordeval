package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;

/**
 * Test for {@link Isograms}.
 */
class IsogramsTest {

  private Isograms evaluator = new Isograms();

  @Test
  void shouldRecognizeIsograms() {
    // given
    String[] words = { "halfduimspyker", "abcdefga", "abcdefgcijk", "jigsaw" };

    // when
    Map<Double, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByScore(evaluator, words);
    assertThat(results, aMapWithSize(2));
    assertThat(results.get(6.0), contains("jigsaw"));
    assertThat(results.get(14.0), contains("halfduimspyker"));
  }
}
