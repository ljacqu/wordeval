package ch.ljacqu.wordeval.evaluation;

import static ch.ljacqu.wordeval.TestUtil.processWords;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class AnagramsTest {

  @Test
  public void shouldFindAnagrams() {
    Anagrams evaluator = new Anagrams();
    // {race, care, acre}, {tea, eat}, {fro, for}, a, something, test
    String[] words = { "race", "for", "a", "eat", "care", "something", "acre", "fro", "tea", "test", "test" };

    processWords(evaluator, words);

    Map<String, Set<String>> results = evaluator.getResults();
    assertThat(results, aMapWithSize(6));
    assertThat(results.get("acer"), containsInAnyOrder("race", "care", "acre"));
    assertThat(results.get("aet"), containsInAnyOrder("tea", "eat"));
    assertThat(results.get("for"), containsInAnyOrder("for", "fro"));
    assertThat(results.get("eghimnost"), contains("something"));
    assertThat(results.get("estt"), contains("test"));
    assertThat(results.get("a"), contains("a"));
  }

}
