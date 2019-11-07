package ch.jalu.wordeval.evaluation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link AlphabeticalSequence}.
 */
class AlphabeticalSequenceTest {

  private AlphabeticalSequence evaluator;

  @BeforeEach
  void initializeEvaluator() {
    evaluator = new AlphabeticalSequence();
  }

  @Test
  void shouldRecognizeWordsWithForwardsSequence() {
    String[] words = { "STUdent", "neMNOgo", "HIJK", "potato", "HIJKen",
        "funGHI", "acdfgg" };

    for (String word : words) {
      evaluator.processWord(word.toLowerCase(), word);
    }
    Map<String, List<String>> results = evaluator.getNavigableResults();

    assertThat(results, aMapWithSize(4));
    assertThat(results.get("ghi"), contains("funGHI"));
    assertThat(results.get("hijk"), containsInAnyOrder("HIJK", "HIJKen"));
    assertThat(results.get("mno"), contains("neMNOgo"));
    assertThat(results.get("stu"), contains("STUdent"));
  }

  @Test
  void shouldRecognizeWordsWithBackwardsSequence() {
    String[] words = { "south", "FEDex", "aJIHaa", "jaPON", "sweet",
        "DCBAffftZYX", "gowkzsr" };

    for (String word : words) {
      evaluator.processWord(word.toLowerCase(), word);
    }
    Map<String, List<String>> results = evaluator.getNavigableResults();

    assertThat(results, aMapWithSize(5));
    assertThat(results.get("fed"), contains("FEDex"));
    assertThat(results.get("jih"), contains("aJIHaa"));
    assertThat(results.get("pon"), contains("jaPON"));
    assertThat(results.get("dcba"), contains("DCBAffftZYX"));
    assertThat(results.get("zyx"), contains("DCBAffftZYX"));
  }
}
