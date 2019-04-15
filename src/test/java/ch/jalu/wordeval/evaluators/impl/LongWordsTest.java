package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link LongWords}.
 */
public class LongWordsTest {

  private LongWords evaluator = new LongWords();

  @Test
  public void shouldAddLongWords() {
    // given
    // 8, 9, 9, 4, 6, 4
    String[] words = { "köszönöm", "piszących", "something", "test", "žodžių", "šalį" };

    // when
    Map<Double, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByScore(evaluator, words);

    // then
    assertThat(results, aMapWithSize(3));
    assertThat(results.get(6.0), containsInAnyOrder("žodžių"));
    assertThat(results.get(8.0), containsInAnyOrder("köszönöm"));
    assertThat(results.get(9.0), containsInAnyOrder("piszących", "something"));
  }

  @Test
  public void shouldProcessCyrillicWords() {
    // given
    // 15, 7, 0, 7
    String[] words = { "Морфологические", "градина", "ушёл", "наречие" };

    // when
    Map<Double, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByScore(evaluator, words);

    // then
    assertThat(results, aMapWithSize(2));
    assertThat(results.get(7.0), containsInAnyOrder("градина", "наречие"));
    assertThat(results.get(15.0), containsInAnyOrder("Морфологические"));
  }

}
