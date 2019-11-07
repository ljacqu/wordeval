package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.TestUtil;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for {@link Isograms}.
 */
class IsogramsTest {

  private Isograms evaluator;

  @BeforeEach
  void initializeEvaluator() {
    evaluator = new Isograms();
  }

  @Test
  void shouldRecognizeIsograms() {
    String[] words = { "halfduimspyker", "abcdefga", "abcdefgcijk", "jigsaw" };

    TestUtil.processWords(evaluator, words);
    Multimap<Integer, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(2));
    assertThat(results.get(6), contains("jigsaw"));
    assertThat(results.get(14), contains("halfduimspyker"));
  }

}
