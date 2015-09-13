package ch.ljacqu.wordeval.evaluation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LetterType;

public class ConsecutiveVowelCountTest {

  private ConsecutiveVowelCount vowelCount;
  private ConsecutiveVowelCount consonantCount;

  @Test
  public void shouldProcessVowelClusters() {
    // 4, 3, 0, 3, {2, 3}, 2
    initializeEvaluators(new Language("en", Alphabet.LATIN));
    String[] words = { "sequoia", "eaux", "abodef", "geëet", "oicąeèl", "ůý" };
    String[] clean = { "sequoia", "eaux", "abodef", "geeet", "oicaeel", "uy" };

    processWords(clean, words);
    Map<Integer, List<String>> vowelResults = vowelCount.getNavigableResults();
    Map<Integer, List<String>> consonantResults = consonantCount
        .getNavigableResults();

    assertThat(consonantResults, anEmptyMap());
    assertThat(vowelResults, aMapWithSize(3));
    assertThat(vowelResults.get(2), containsInAnyOrder("oicąeèl", "ůý"));
    assertThat(vowelResults.get(3),
        containsInAnyOrder("eaux", "geëet", "oicąeèl"));
    assertThat(vowelResults.get(4), contains("sequoia"));
  }

  @Test
  public void shouldProcessConsonantClusters() {
    // {3, 2}, 0, 3, {4, 3}, 0
    initializeEvaluators(new Language("en", Alphabet.LATIN));
    String[] words = { "pfrund", "potato", "przy", "wśrżystkęm", "arigato" };
    String[] clean = { "pfrund", "potato", "przy", "wsrzystkem", "arigato" };

    processWords(clean, words);
    Map<Integer, List<String>> vowelResults = vowelCount.getNavigableResults();
    Map<Integer, List<String>> consonantResults = consonantCount
        .getNavigableResults();

    assertThat(vowelResults, anEmptyMap());
    assertThat(consonantResults, aMapWithSize(3));
    assertThat(consonantResults.get(2), contains("pfrund"));
    assertThat(consonantResults.get(3),
        containsInAnyOrder("pfrund", "przy", "wśrżystkęm"));
    assertThat(consonantResults.get(4), contains("wśrżystkęm"));
  }

  @Test
  public void shouldProcessCyrillicWords() {
    initializeEvaluators(new Language("ru", Alphabet.CYRILLIC));
    // Vowels: 2, 2, 0, 0 | Consonants: 0, 2, {3, 4, 2}, {2, 2}
    // ь is neither consonant nor vowel
    String[] words = { "википедия", "вооружённый", "здравствуйте", "апрельская" };

    processWords(words);
    Map<Integer, List<String>> vowelResults = vowelCount.getNavigableResults();
    Map<Integer, List<String>> consonantResults = consonantCount
        .getNavigableResults();

    assertThat(vowelResults, aMapWithSize(1));
    assertThat(vowelResults.get(2),
        containsInAnyOrder("википедия", "вооружённый", "апрельская"));
    assertThat(consonantResults, aMapWithSize(3));
    assertThat(consonantResults.get(2),
        containsInAnyOrder("вооружённый", "здравствуйте", "апрельская"));
    assertThat(consonantResults.get(3), contains("здравствуйте"));
    assertThat(consonantResults.get(4), contains("здравствуйте"));
  }

  @Test
  public void shouldProcessBulgarianCorrectly() {
    Language lang = new Language("bg", Alphabet.CYRILLIC)
        .setAdditionalVowels("ъ");
    initializeEvaluators(lang);
    // Vowels: 2, 2, 0; Consonants: 3, 2, 0
    String[] words = { "възприема", "неъзабза", "обособени" };

    processWords(words);
    Map<Integer, List<String>> vowelResults = vowelCount.getNavigableResults();
    Map<Integer, List<String>> consonantResults = consonantCount
        .getNavigableResults();

    assertThat(vowelResults, aMapWithSize(1));
    assertThat(vowelResults.get(2), containsInAnyOrder("възприема", "неъзабза"));
    assertThat(consonantResults, aMapWithSize(2));
    assertThat(consonantResults.get(2), contains("неъзабза"));
    assertThat(consonantResults.get(3), contains("възприема"));
  }

  private void initializeEvaluators(Language language) {
    vowelCount = new ConsecutiveVowelCount(LetterType.VOWELS, language);
    consonantCount = new ConsecutiveVowelCount(LetterType.CONSONANTS, language);
  }

  private void processWords(String[] words) {
    processWords(words, words);
  }

  private void processWords(String[] cleanWords, String[] words) {
    for (int i = 0; i < cleanWords.length; ++i) {
      vowelCount.processWord(cleanWords[i], words[i]);
      consonantCount.processWord(cleanWords[i], words[i]);
    }
  }

}
