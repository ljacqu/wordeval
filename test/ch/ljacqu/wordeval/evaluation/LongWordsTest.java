package ch.ljacqu.wordeval.evaluation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class LongWordsTest {

  private LongWords longWords;

  @Before
  public void setUpLongWords() {
    longWords = new LongWords();
  }

  private void processWord(String word) {
    longWords.processWord(word, word);
  }

  @Test
  public void shouldAddLongWords() {
    // 8, 9, 9, 4, 6, 4
    String[] words = { "köszönöm", "piszących", "something", "test", "žodžių",
        "šalį" };

    for (String word : words) {
      processWord(word);
    }
    Map<Integer, Set<String>> results = longWords.getResults();

    assertThat(results, aMapWithSize(3));
    assertThat(results.get(4), nullValue());
    assertThat(results.get(6), containsInAnyOrder("žodžių"));
    assertThat(results.get(8), containsInAnyOrder("köszönöm"));
    assertThat(results.get(9), containsInAnyOrder("piszących", "something"));
  }

  @Test
  public void shouldProcessCyrillicWords() {
    // 15, 7, 0, 7
    String[] words = { "Морфологические", "градина", "ушёл", "наречие" };

    for (String word : words) {
      processWord(word);
    }
    Map<Integer, Set<String>> results = longWords.getResults();
    assertThat(results, aMapWithSize(2));
    assertThat(results.get(7), containsInAnyOrder("градина", "наречие"));
    assertThat(results.get(15), containsInAnyOrder("Морфологические"));
  }

}
