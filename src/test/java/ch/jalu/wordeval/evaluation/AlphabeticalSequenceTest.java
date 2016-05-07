package ch.jalu.wordeval.evaluation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class AlphabeticalSequenceTest {

  private AlphabeticalSequence evaluator;

  @Before
  public void initializeEvaluator() {
    evaluator = new AlphabeticalSequence();
  }

  @Test
  public void shouldRecognizeWordsWithForwardsSequence() {
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
  public void shouldRecognizeWordsWithBackwardsSequence() {
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
