package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class SameLetterConsecutiveTest {

  private SameLetterConsecutive evaluator;

  @Before
  public void initializeEvaluator() {
    evaluator = new SameLetterConsecutive();
  }

  @Test
  public void shouldRecognizeConsecutiveLetters() {
    String[] words = { "hello", "Schifffahrt", "geëet", "window", "Töfffahrer",
        "schnell", "Llama" };
    String[] cleanWords = { "hello", "schifffahrt", "geeet", "window",
        "töfffahrer", "schnell", "llama" };

    for (int i = 0; i < cleanWords.length; ++i) {
      evaluator.processWord(cleanWords[i], words[i]);
    }
    Map<String, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 3);
    assertEquals(results.get("ll").size(), 3);
    assertEquals(results.get("fff").size(), 2);
    assertEquals(results.get("fff").get(0), "Schifffahrt");
    assertEquals(results.get("eee").size(), 1);
    assertEquals(results.get("eee").get(0), "geëet");
  }

  @Test
  public void shouldRecognizeSeparateOccurrences() {
    // {sss,bb}, {ss,pp}, {aa,ff}, {ooo,ee,oo}
    String[] words = { "Massstabbrecher", "Reisstopp", "aabeffo", "oooeemoo" };

    for (String word : words) {
      evaluator.processWord(word, word);
    }
    Map<String, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 9);
    assertEquals(results.get("sss").size(), 1);
    assertEquals(results.get("ss").size(), 1);
    assertEquals(results.get("ooo").size(), 1);
    assertEquals(results.get("oo").size(), 1);
    assertEquals(results.get("pp").size(), 1);
    assertEquals(results.get("ff").get(0), "aabeffo");
    assertEquals(results.get("ooo"), results.get("ee"));
    assertEquals(results.get("pp"), results.get("ss"));
  }

  @Test
  public void shouldProcessCyrillicWords() {
    String[] words = { "старинного", "величайший", "поддержки", "Избранные",
        "независимая" };
    String[] cleanWords = { "старинного", "величайший", "поддержки",
        "избранные", "независимая" };

    // TODO ensure that ё and е are considered equivalent here

    for (int i = 0; i < cleanWords.length; ++i) {
      evaluator.processWord(cleanWords[i], words[i]);
    }
    Map<String, List<String>> results = evaluator.getResults();

    assertEquals(results.size(), 2);
    assertEquals(results.get("дд").size(), 1);
    assertEquals(results.get("нн").size(), 2);
    assertEquals(results.get("нн").get(1), "Избранные");
  }

}
