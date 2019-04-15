package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Isograms}.
 */
public class IsogramsTest {

  private Isograms evaluator = new Isograms();

  @Test
  public void shouldRecognizeIsograms() {
    // given
    String[] words = { "halfduimspyker", "abcdefga", "abcdefgcijk", "jigsaw" };

    // when
    Map<Double, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByScore(evaluator, words);
    assertThat(results, aMapWithSize(2));
    assertThat(results.get(6.0), contains("jigsaw"));
    assertThat(results.get(14.0), contains("halfduimspyker"));
  }
}
