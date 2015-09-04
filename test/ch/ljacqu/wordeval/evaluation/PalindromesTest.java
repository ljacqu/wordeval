package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class PalindromesTest {

  private Palindromes evaluator;

  @Before
  public void initializeEvaluator() {
    evaluator = new Palindromes();
  }

  @Test
  public void shouldRecognizePalindromes() {
    // otto, bagab, -, awkwa, bab, bab/ili
    String[] words = { "trottoir", "ebagabo", "palindrome", "awkward",
        "probable", "probability", "o" };

    for (String word : words) {
      evaluator.processWord(word, word);
    }
    Map<String, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 5);
    assertNotNull(results.get("otto"));
    assertNotNull(results.get("bagab"));
    assertNotNull(results.get("awkwa"));
    assertNotNull(results.get("bab"));
    assertEquals(results.get("awkwa").get(0), "awkward");
    assertEquals(results.get("bab").size(), 2);
    assertEquals(results.get("bab").get(0), "probable");
    assertEquals(results.get("bab").get(1), results.get("ili").get(0));
  }

  @Test
  public void shouldNotAddSimplePairs() {
    String[] words = { "gaaf", "aardvaark", "letter", "boot", "bleed" };

    for (String word : words) {
      evaluator.processWord(word, word);
    }
    Map<String, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 1);
    assertEquals(results.get("ette").get(0), "letter");
  }

}
