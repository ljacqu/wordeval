package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link LongWords}.
 */
class LongWordsTest extends AbstractEvaluatorTest {

  private final LongWords longWords = new LongWords();

  @Test
  void shouldAddLongWords() {
    // given
    // 8, 9, 9, 4, 6, 4
    List<Word> words = createWords("köszönöm", "piszących", "something", "test", "žodžių", "šalį");

    // when
    longWords.evaluate(words);

    // then
    Map<Double, Set<String>> results = groupByScore(longWords.getResults());
    assertThat(results, aMapWithSize(3));
    assertThat(results.get(6.0), containsInAnyOrder("žodžių"));
    assertThat(results.get(8.0), containsInAnyOrder("köszönöm"));
    assertThat(results.get(9.0), containsInAnyOrder("piszących", "something"));
  }

  @Test
  void shouldProcessCyrillicWords() {
    // given
    // 15, 7, 0, 7
    List<Word> words = createWords("Морфологические", "градина", "ушёл", "наречие");

    // when
    longWords.evaluate(words);

    // then
    Map<Double, Set<String>> results = groupByScore(longWords.getResults());
    assertThat(results, aMapWithSize(2));
    assertThat(results.get(7.0), containsInAnyOrder("градина", "наречие"));
    assertThat(results.get(15.0), containsInAnyOrder("Морфологические"));
  }
}
