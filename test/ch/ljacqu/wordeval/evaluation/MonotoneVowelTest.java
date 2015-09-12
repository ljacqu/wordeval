package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import ch.ljacqu.wordeval.language.LetterService;
import ch.ljacqu.wordeval.language.LetterType;

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
      String noAccentWord = LetterService.removeAccentsFromWord(word);
      vowelEvaluator.processWord(noAccentWord, word);
      consonantEvaluator.processWord(noAccentWord, word);
    }
  }

  @Test
  public void shouldProcessWordsCorrectly() {
    // lengths are 10, 9, 5, 5, 10, 8, 8, 0
    String[] words = { "assosiasie", "something", "flûte", "geëet",
        "mâdagascar", "tatoťute", "čocaçoći", "eau" };
    process(words);

    Map<Integer, List<String>> vowelResults = vowelEvaluator
        .getNavigableResults();
    Map<Integer, List<String>> consonantResults = consonantEvaluator
        .getNavigableResults();

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
