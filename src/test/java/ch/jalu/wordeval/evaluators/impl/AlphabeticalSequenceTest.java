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
 * Test for {@link AlphabeticalSequence}.
 */
class AlphabeticalSequenceTest extends AbstractEvaluatorTest {

  private final AlphabeticalSequence alphabeticalSequence = new AlphabeticalSequence();

  @Test
  void shouldRecognizeWordsWithForwardsSequence() {
    // given
    List<Word> words = createWords("student", "nemnogo", "hijk", "potato", "hijken", "funghi", "acdfgg");

    // when
    alphabeticalSequence.evaluate(words);

    // then
    Map<String, Set<String>> results = groupByKey(alphabeticalSequence.getResults());
    assertThat(results, aMapWithSize(4));
    assertThat(results.get("ghi"), contains("funghi"));
    assertThat(results.get("hijk"), containsInAnyOrder("hijk", "hijken"));
    assertThat(results.get("mno"), contains("nemnogo"));
    assertThat(results.get("stu"), contains("student"));
  }

  @Test
  void shouldRecognizeWordsWithBackwardsSequence() {
    // given
    List<Word> words = createWords("south", "fedex", "ajihaa", "japon", "sweet", "dcbaffftzyx", "gowkzsr");

    // when
    alphabeticalSequence.evaluate(words);

    // then
    Map<String, Set<String>> results = groupByKey(alphabeticalSequence.getResults());
    assertThat(results, aMapWithSize(5));
    assertThat(results.get("fed"), contains("fedex"));
    assertThat(results.get("jih"), contains("ajihaa"));
    assertThat(results.get("pon"), contains("japon"));
    assertThat(results.get("dcba"), contains("dcbaffftzyx"));
    assertThat(results.get("zyx"), contains("dcbaffftzyx"));
  }
}
