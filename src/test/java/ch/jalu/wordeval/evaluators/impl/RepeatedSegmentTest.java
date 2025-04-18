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
 * Test for {@link RepeatedSegment}.
 */
class RepeatedSegmentTest extends AbstractEvaluatorTest {

  private final RepeatedSegment repeatedSegment = new RepeatedSegment();

  @Test
  void shouldFindMatches() {
    // given
    // 3x est; 2x an; 2x ssi, 2x iss; 2x an, 2x na; -; 2x er; 2x bar
    List<Word> words = createWords(
        "geestestoestand", "banane", "mississippi", "ananas", "something", "derber", "barbar");

    // when
    repeatedSegment.evaluate(words);

    // then
    Map<String, Set<String>> results = flattenKeyAndScore(repeatedSegment.getResults());
    assertThat(results, aMapWithSize(7));
    assertThat(results.get("3,est"), contains("geestestoestand"));
    assertThat(results.get("2,an"), containsInAnyOrder("banane", "ananas"));
    assertThat(results.get("2,ssi"), contains("mississippi"));
    assertThat(results.get("2,iss"), contains("mississippi"));
    assertThat(results.get("2,na"), contains("ananas"));
    assertThat(results.get("2,er"), contains("derber"));
    assertThat(results.get("2,bar"), contains("barbar"));
  }
}
