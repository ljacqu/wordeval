package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ch.ljacqu.wordeval.LetterService;
import ch.ljacqu.wordeval.LetterType;

public class ConsecutiveVowelCountTest {

  private ConsecutiveVowelCount vowelCount;
  private ConsecutiveVowelCount consonantCount;

  @Before
  public void initializeEvaluator() {
    vowelCount = new ConsecutiveVowelCount(LetterType.VOWELS);
    consonantCount = new ConsecutiveVowelCount(LetterType.CONSONANTS);
  }

  private void processWords(String[] words) {
    for (String word : words) {
      String accentLessWord = LetterService.removeAccentsFromWord(word);
      vowelCount.processWord(accentLessWord, word);
      consonantCount.processWord(accentLessWord, word);
    }
  }

  @Test
  public void shouldProcessVowelClusters() {
    String[] words = { "sequoia", "geëet", "abodef", "eaux", "oicąeèl", "ůý" };

    processWords(words);
    Map<Integer, List<String>> vowelResults = vowelCount.getResults();
    Map<Integer, List<String>> consonantResults = consonantCount.getResults();

    assertTrue(consonantResults.isEmpty());
    assertFalse(vowelResults.isEmpty());
    assertEquals(vowelResults.get(4).size(), 1);
    assertEquals(vowelResults.get(4).get(0), "sequoia");
    assertEquals(vowelResults.get(3).size(), 3);
    assertEquals(vowelResults.get(3).get(1), "eaux");
    assertEquals(vowelResults.get(2).size(), 2);
  }

  @Test
  public void shouldProcessConsonantClusters() {
    String[] words = { "pfrund", "potato", "przy", "wśrżystkęm", "arigato" };

    processWords(words);
    Map<Integer, List<String>> vowelResults = vowelCount.getResults();
    Map<Integer, List<String>> consonantResults = consonantCount.getResults();

    assertTrue(vowelResults.isEmpty());
    assertFalse(consonantResults.isEmpty());
    assertEquals(consonantResults.size(), 3);
    assertEquals(consonantResults.get(2).size(), 1);
    assertEquals(consonantResults.get(3).size(), 3);
    assertEquals(consonantResults.get(3).get(1), "przy");
    assertEquals(consonantResults.get(4).size(), 1);
    assertNull(consonantResults.get(5));
  }

  @Ignore
  @Test
  // TODO #12: implement Cyrillic logic
  public void shouldProcessCyrillicWords() {
    String[] word = { "Википедия", "Вооружённый" };

    processWords(word);

    Map<Integer, List<String>> vowelResults = vowelCount.getResults();
    Map<Integer, List<String>> consonantResults = consonantCount.getResults();

    assertEquals(vowelResults.size(), 1);
    assertEquals(consonantResults.size(), 1);
    assertEquals(vowelResults.get(2), 2);
    assertEquals(consonantResults.get(2), 1);

    // TODO: test Bulgarian-specific words
  }

}