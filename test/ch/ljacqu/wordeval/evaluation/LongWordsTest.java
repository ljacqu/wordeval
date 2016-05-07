package ch.ljacqu.wordeval.evaluation;

import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import static ch.ljacqu.wordeval.TestUtil.processWords;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@SuppressWarnings("javadoc")
public class LongWordsTest {

  private LongWords longWords;

  @Before
  public void setUpLongWords() {
    longWords = new LongWords();
  }

  @Test
  public void shouldAddLongWords() {
    // 8, 9, 9, 4, 6, 4
    String[] words = { "köszönöm", "piszących", "something", "test", "žodžių",
        "šalį" };

    processWords(longWords, words);
    Multimap<Integer, String> results = longWords.getResults();

    assertThat(results.keySet(), hasSize(3));
    assertThat(results.get(4), empty());
    assertThat(results.get(6), containsInAnyOrder("žodžių"));
    assertThat(results.get(8), containsInAnyOrder("köszönöm"));
    assertThat(results.get(9), containsInAnyOrder("piszących", "something"));
  }

  @Test
  public void shouldProcessCyrillicWords() {
    // 15, 7, 0, 7
    String[] words = { "Морфологические", "градина", "ушёл", "наречие" };

    processWords(longWords, words);
    Multimap<Integer, String> results = longWords.getResults();
    assertThat(results.keySet(), hasSize(2));
    assertThat(results.get(7), containsInAnyOrder("градина", "наречие"));
    assertThat(results.get(15), containsInAnyOrder("Морфологические"));
  }

}
