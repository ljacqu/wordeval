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
import ch.ljacqu.wordeval.LetterType;

public class ConsecutiveVowelCountTest {

  private ConsecutiveVowelCount vowelCount;
  private ConsecutiveVowelCount consonantCount;

  @Before
  public void initializeEvaluator() {
    vowelCount = new ConsecutiveVowelCount(LetterType.VOWELS);
    consonantCount = new ConsecutiveVowelCount(LetterType.CONSONANTS);
  }

  private void processWords(String[] cleanWords, String[] words) {
    for (int i = 0; i < cleanWords.length; ++i) {
      vowelCount.processWord(cleanWords[i], words[i]);
      consonantCount.processWord(cleanWords[i], words[i]);
    }
  }

  @Test
  public void shouldProcessVowelClusters() {
    // 4, 3, 0, 3, 3
    String[] words = { "sequoia", "eaux", "abodef", "geëet", "oicąeèl", "ůý" };
    String[] clean = { "sequoia", "eaux", "abodef", "geeet", "oicaeel", "uy" };

    processWords(clean, words);
    Map<Integer, List<String>> vowelResults = vowelCount.getNavigableResults();
    Map<Integer, List<String>> consonantResults = consonantCount
        .getNavigableResults();

    assertTrue(consonantResults.isEmpty());
    assertFalse(vowelResults.isEmpty());
    assertEquals(vowelResults.get(4).size(), 1);
    assertEquals(vowelResults.get(4).get(0), "sequoia");
    assertEquals(vowelResults.get(3).size(), 3);
    assertEquals(vowelResults.get(3).get(1), "geëet");
    assertEquals(vowelResults.get(2).size(), 2);
  }

  @Test
  public void shouldProcessConsonantClusters() {
    // 3, 0, 3, 4, 0
    String[] words = { "pfrund", "potato", "przy", "wśrżystkęm", "arigato" };
    String[] clean = { "pfrund", "potato", "przy", "wsrzystkem", "arigato" };

    processWords(clean, words);
    Map<Integer, List<String>> vowelResults = vowelCount.getNavigableResults();
    Map<Integer, List<String>> consonantResults = consonantCount
        .getNavigableResults();

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
    String[] words = { "Википедия", "Вооружённый" };
    String[] clean = { "Википедия", "Вооруженный" };

    processWords(clean, words);

    Map<Integer, List<String>> vowelResults = vowelCount.getNavigableResults();
    Map<Integer, List<String>> consonantResults = consonantCount
        .getNavigableResults();

    assertEquals(vowelResults.size(), 1);
    assertEquals(consonantResults.size(), 1);
    assertEquals(vowelResults.get(2), 2);
    assertEquals(consonantResults.get(2), 1);

    // TODO #12: test Bulgarian-specific words
  }

}
