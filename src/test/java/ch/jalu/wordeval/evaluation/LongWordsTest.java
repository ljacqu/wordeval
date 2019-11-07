package ch.jalu.wordeval.evaluation;

import com.google.common.collect.Multimap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.jalu.wordeval.TestUtil.processWords;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for {@link LongWords}.
 */
class LongWordsTest {

  private LongWords longWords;

  @BeforeEach
  void setUpLongWords() {
    longWords = new LongWords();
  }

  @Test
  void shouldAddLongWords() {
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
  void shouldProcessCyrillicWords() {
    // 15, 7, 0, 7
    String[] words = { "Морфологические", "градина", "ушёл", "наречие" };

    processWords(longWords, words);
    Multimap<Integer, String> results = longWords.getResults();
    assertThat(results.keySet(), hasSize(2));
    assertThat(results.get(7), containsInAnyOrder("градина", "наречие"));
    assertThat(results.get(15), containsInAnyOrder("Морфологические"));
  }

}
