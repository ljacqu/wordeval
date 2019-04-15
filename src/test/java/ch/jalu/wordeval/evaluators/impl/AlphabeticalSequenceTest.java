package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link AlphabeticalSequence}.
 */
public class AlphabeticalSequenceTest {

  private AlphabeticalSequence evaluator = new AlphabeticalSequence();

  @Test
  public void shouldRecognizeWordsWithForwardsSequence() {
    // given
    String[] words = { "student", "nemnogo", "hijk", "potato", "hijken", "funghi", "acdfgg" };

    // when
    Map<String, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByKey(evaluator, words);

    // then
    assertThat(results, aMapWithSize(4));
    assertThat(results.get("ghi"), contains("funghi"));
    assertThat(results.get("hijk"), containsInAnyOrder("hijk", "hijken"));
    assertThat(results.get("mno"), contains("nemnogo"));
    assertThat(results.get("stu"), contains("student"));
  }

  @Test
  public void shouldRecognizeWordsWithBackwardsSequence() {
    // given
    String[] words = { "south", "fedex", "ajihaa", "japon", "sweet", "dcbaffftzyx", "gowkzsr" };

    // when
    Map<String, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupByKey(evaluator, words);

    // then
    assertThat(results, aMapWithSize(5));
    assertThat(results.get("fed"), contains("fedex"));
    assertThat(results.get("jih"), contains("ajihaa"));
    assertThat(results.get("pon"), contains("japon"));
    assertThat(results.get("dcba"), contains("dcbaffftzyx"));
    assertThat(results.get("zyx"), contains("dcbaffftzyx"));
  }
}
