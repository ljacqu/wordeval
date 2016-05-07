package ch.jalu.wordeval.evaluation;

import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import static ch.jalu.wordeval.TestUtil.processWords;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class SameLetterConsecutiveTest {

  private SameLetterConsecutive evaluator;

  @Before
  public void initializeEvaluator() {
    evaluator = new SameLetterConsecutive();
  }

  @Test
  public void shouldRecognizeConsecutiveLetters() {
    // ll, fff, eee, -, fff, ll, ll
    String[] words = { "hello", "Schifffahrt", "geëet", "window", "Töfffahrer",
        "schnell", "Llama" };
    String[] cleanWords = { "hello", "schifffahrt", "geeet", "window",
        "töfffahrer", "schnell", "llama" };

    processWords(evaluator, cleanWords, words);
    Multimap<String, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(3));
    assertThat(results.get("ll"),
        containsInAnyOrder("hello", "schnell", "Llama"));
    assertThat(results.get("fff"),
        containsInAnyOrder("Schifffahrt", "Töfffahrer"));
    assertThat(results.get("eee"), containsInAnyOrder("geëet"));
  }

  @Test
  public void shouldRecognizeSeparateOccurrences() {
    // {sss,bb}, {ss,pp}, {aa,ss}, {ooo,ee,oo}
    String[] words = { "Massstabbrecher", "Reisstopp", "aabesso", "oooeemoo" };

    processWords(evaluator, words);
    Multimap<String, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(8));
    assertThat(results.get("aa"), containsInAnyOrder("aabesso"));
    assertThat(results.get("bb"), containsInAnyOrder("Massstabbrecher"));
    assertThat(results.get("ee"), containsInAnyOrder("oooeemoo"));
    assertThat(results.get("oo"), containsInAnyOrder("oooeemoo"));
    assertThat(results.get("pp"), containsInAnyOrder("Reisstopp"));
    assertThat(results.get("ss"), containsInAnyOrder("Reisstopp", "aabesso"));
    assertThat(results.get("ooo"), containsInAnyOrder("oooeemoo"));
    assertThat(results.get("sss"), containsInAnyOrder("Massstabbrecher"));
  }

  @Test
  public void shouldProcessCyrillicWords() {
    // нн, -, дд, нн, -
    String[] words = { "Избранные", "величайший", "поддержки", "старинного",
        "независимая" };
    String[] cleanWords = { "избранные", "величайший", "поддержки",
        "старинного", "независимая" };

    processWords(evaluator, cleanWords, words);
    Multimap<String, String> results = evaluator.getResults();

    assertThat(results.keySet(), hasSize(2));
    assertThat(results.get("дд"), contains("поддержки"));
    assertThat(results.get("нн"), containsInAnyOrder("Избранные", "старинного"));
  }

}
