package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;

/**
 * Test for {@link Isograms}.
 */
class IsogramsTest extends AbstractEvaluatorTest {

  private final Isograms isograms = new Isograms();

  @Test
  void shouldRecognizeIsograms() {
    // given
    List<Word> words = createWords("halfduimspyker", "abcdefga", "abcdefgcijk", "jigsaw");

    // when
    isograms.evaluate(words);

    // then
    Map<Double, Set<String>> results = groupByScore(isograms.getResults());
    assertThat(results, aMapWithSize(2));
    assertThat(results.get(6.0), contains("jigsaw"));
    assertThat(results.get(14.0), contains("halfduimspyker"));
  }
}
