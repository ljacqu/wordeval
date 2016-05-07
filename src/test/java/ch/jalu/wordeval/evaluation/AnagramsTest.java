package ch.jalu.wordeval.evaluation;

import com.google.common.collect.Multimap;
import org.junit.Test;

import static ch.jalu.wordeval.TestUtil.processWords;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class AnagramsTest {

  @Test
  public void shouldFindAnagrams() {
    Anagrams evaluator = new Anagrams();
    // {race, care, acre}, {tea, eat}, {fro, for}, a, something, test
    String[] words = { "race", "for", "a", "eat", "care", "something", "acre", "fro", "tea", "test", "test" };

    processWords(evaluator, words);

    Multimap<String, String> results = evaluator.getResults();
    assertThat(results.keySet(), hasSize(6));
    assertThat(results.get("acer"), containsInAnyOrder("race", "care", "acre"));
    assertThat(results.get("aet"), containsInAnyOrder("tea", "eat"));
    assertThat(results.get("for"), containsInAnyOrder("for", "fro"));
    assertThat(results.get("eghimnost"), contains("something"));
    assertThat(results.get("estt"), contains("test"));
    assertThat(results.get("a"), contains("a"));
  }

}
