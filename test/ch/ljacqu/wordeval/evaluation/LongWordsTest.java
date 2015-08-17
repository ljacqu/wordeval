package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class LongWordsTest {

  private LongWords longWords;

  @Before
  public void setUpLongWords() {
    longWords = new LongWords(6);
  }

  private void processWord(String word) {
    longWords.processWord(word, word);
  }

  @Test
  public void shouldAddLongWords() {
    String[] words = { "test", "something", "piszących", "köszönöm", "žodžių",
        "šalį" };

    for (String word : words) {
      processWord(word);
    }
    Map<Integer, List<String>> results = longWords.getResults();

    assertNull(results.get(4));
    assertEquals(results.size(), 3);
    assertEquals(results.get(9).size(), 2);
    assertEquals(results.get(9).get(0), "something");
    assertEquals(results.get(9).get(1), "piszących");
    assertFalse(results.get(8).isEmpty());
    assertEquals(results.get(6).size(), 1);
    assertEquals(results.get(6).get(0), "žodžių");
  }

  @Test
  public void shouldProcessCyrillicWords() {
    String[] words = { "Морфологические", "наречие", "ушёл", "градина" };

    for (String word : words) {
      processWord(word);
    }
    Map<Integer, List<String>> results = longWords.getResults();
    assertEquals(results.size(), 2);
    assertEquals(results.get(15).get(0), "Морфологические");
    assertEquals(results.get(7).size(), 2);
    assertEquals(results.get(7).get(1), "градина");
  }

}
