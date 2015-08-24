package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class AlphabeticSequenceTest {

  private AlphabeticSequence evaluator;

  @Before
  public void initializeEvaluator() {
    evaluator = new AlphabeticSequence();
  }

  @Test
  public void shouldRecognizeWordsWithForwardsSequence() {
    String[] words = { "STUdent", "neMNOgo", "HIJKen", "potato", "HIJK",
        "funGHI", "acdfgg" };

    for (String word : words) {
      evaluator.processWord(word.toLowerCase(), word);
    }
    Map<String, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 4);
    assertEquals(results.get("hijk").size(), 2);
    assertEquals(results.get("stu").size(), 1);
    assertEquals(results.get("mno").size(), 1);

    assertEquals(results.get("mno").get(0), "neMNOgo");
    assertEquals(results.get("hijk").get(1), "HIJK");
  }
  
  @Test
  public void shouldRecognizeWordsWithBackwardsSequence() {
    String[] words = { "south", "FEDex", "aJIHaa", "jaPON", "sweet",
        "DCBAffftZYX", "gowkzsr" };

    for (String word : words) {
      evaluator.processWord(word.toLowerCase(), word);
    }
    Map<String, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 5);
    assertEquals(results.get("fed").size(), 1);
    assertEquals(results.get("jih").size(), 1);
    assertEquals(results.get("pon").size(), 1);
    assertEquals(results.get("dcba").size(), 1);
    assertEquals(results.get("zyx").size(), 1);

    assertEquals(results.get("zyx"), results.get("dcba"));
    assertEquals(results.get("pon").get(0), "jaPON");
  }
}
