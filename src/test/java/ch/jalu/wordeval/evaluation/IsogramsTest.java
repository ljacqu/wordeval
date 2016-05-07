package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.TestUtil;
import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import static ch.jalu.wordeval.TestUtil.processWords;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class IsogramsTest {

  private Isograms evaluator;

  @Before
  public void initializeEvaluator() {
    evaluator = new Isograms();
  }

  @Test
  public void shouldRecognizeIsograms() {
    String[] words = { "halfduimspyker", "abcdefga", "abcdefgcijk", "jigsaw" };

    TestUtil.processWords(evaluator, words);
    Multimap<Integer, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(2));
    assertThat(results.get(6), contains("jigsaw"));
    assertThat(results.get(14), contains("halfduimspyker"));
  }

}
