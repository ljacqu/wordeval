package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class MonotoneVowelTest {

  private MonotoneVowel vowelEvaluator;
  private MonotoneVowel consonantEvaluator;

  @Before
  public void initializeEvaluator() {
    vowelEvaluator = new MonotoneVowel(LetterType.VOWELS);
    consonantEvaluator = new MonotoneVowel(LetterType.CONSONANTS);
  }

  private void process(String[] words) {
    for (String word : words) {
      vowelEvaluator.processWord(word, word);
      consonantEvaluator.processWord(word, word);
    }
  }

  @Test
  public void shouldProcessWordsCorrectly() {
    // lengths are 10, 9, 5, 10, 8, 8
    String[] words = { "assosiasie", "something", "geëet", "mâdagascar",
        "tatoťute", "čocaçoći" };
    process(words);

    Map<Integer, List<String>> vowelResults = vowelEvaluator.getResults();
    Map<Integer, List<String>> consonantResults = consonantEvaluator
        .getResults();

    assertEquals(vowelResults.size(), 2);
    assertEquals(vowelResults.get(10).size(), 1);
    assertEquals(vowelResults.get(5).size(), 1);
    assertEquals(vowelResults.get(10).get(0), "mâdagascar");

    assertEquals(consonantResults.size(), 2);
    assertEquals(consonantResults.get(10).size(), 1);
    assertEquals(consonantResults.get(10).get(0), "assosiasie");
    assertEquals(consonantResults.get(8).size(), 2);
  }

}
