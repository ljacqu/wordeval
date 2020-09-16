package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link ConsecutiveVowelCount}.
 */
class ConsecutiveVowelCountTest {

  private ConsecutiveVowelCount vowelCount;
  private ConsecutiveVowelCount consonantCount;

  @Test
  void shouldProcessVowelClusters() {
    // given
    // 4, 3, 0, 3, {2, 3}, 2
    initializeEvaluators(TestUtil.newLanguage("en", Alphabet.LATIN).build());
    String[] words = { "sequoia", "eaux", "abodef", "geeet", "oicaeel", "uy" };

    // when
    Map<Double, Set<String>> vowelResults = EvaluatorTestHelper.evaluateAndGroupByScore(vowelCount, words);
    Map<Double, Set<String>> consonantResults = EvaluatorTestHelper.evaluateAndGroupByScore(consonantCount, words);

    // then
    assertThat(consonantResults, anEmptyMap());
    assertThat(vowelResults, aMapWithSize(3));
    assertThat(vowelResults.get(2.0), containsInAnyOrder("oicaeel", "uy"));
    assertThat(vowelResults.get(3.0), containsInAnyOrder("eaux", "geeet", "oicaeel"));
    assertThat(vowelResults.get(4.0), contains("sequoia"));
  }

  @Test
  void shouldProcessConsonantClusters() {
    // given
    // {3, 2}, 0, 3, {4, 3}, 0
    initializeEvaluators(TestUtil.newLanguage("en", Alphabet.LATIN).build());
    String[] words = { "pfrund", "potato", "przy", "wsrzystkem", "arigato" };

    // when
    Map<Double, Set<String>> vowelResults = EvaluatorTestHelper.evaluateAndGroupByScore(vowelCount, words);
    Map<Double, Set<String>> consonantResults = EvaluatorTestHelper.evaluateAndGroupByScore(consonantCount, words);

    // then
    assertThat(vowelResults, anEmptyMap());
    assertThat(consonantResults, aMapWithSize(3));
    assertThat(consonantResults.get(2.0), contains("pfrund"));
    assertThat(consonantResults.get(3.0), containsInAnyOrder("pfrund", "przy", "wsrzystkem"));
    assertThat(consonantResults.get(4.0), contains("wsrzystkem"));
  }

  @Test
  void shouldProcessCyrillicWords() {
    // given
    initializeEvaluators(TestUtil.newLanguage("ru", Alphabet.CYRILLIC).build());
    // Vowels: 2, 2, 0, 0 | Consonants: 0, 2, {3, 4, 2}, {2, 2}
    // ь is neither consonant nor vowel
    String[] words = { "википедия", "вооружённый", "здравствуйте", "апрельская" };

    // when
    Map<Double, Set<String>> vowelResults = EvaluatorTestHelper.evaluateAndGroupByScore(vowelCount, words);
    Map<Double, Set<String>> consonantResults = EvaluatorTestHelper.evaluateAndGroupByScore(consonantCount, words);

    // then
    assertThat(vowelResults, aMapWithSize(1));
    assertThat(vowelResults.get(2.0), containsInAnyOrder("википедия", "вооружённый", "апрельская"));
    assertThat(consonantResults, aMapWithSize(3));
    assertThat(consonantResults.get(2.0), containsInAnyOrder("вооружённый", "здравствуйте", "апрельская"));
    assertThat(consonantResults.get(3.0), contains("здравствуйте"));
    assertThat(consonantResults.get(4.0), contains("здравствуйте"));
  }

  @Test
  void shouldProcessBulgarianCorrectly() {
    // given
    Language lang = TestUtil.newLanguage("bg", Alphabet.CYRILLIC).additionalVowels("ъ").build();
    initializeEvaluators(lang);
    // Vowels: 2, 2, 0; Consonants: 3, 2, 0
    String[] words = { "възприема", "неъзабза", "обособени" };

    // when
    Map<Double, Set<String>> vowelResults = EvaluatorTestHelper.evaluateAndGroupByScore(vowelCount, words);
    Map<Double, Set<String>> consonantResults = EvaluatorTestHelper.evaluateAndGroupByScore(consonantCount, words);

    // then
    assertThat(vowelResults, aMapWithSize(1));
    assertThat(vowelResults.get(2.0), containsInAnyOrder("възприема", "неъзабза"));
    assertThat(consonantResults, aMapWithSize(2));
    assertThat(consonantResults.get(2.0), contains("неъзабза"));
    assertThat(consonantResults.get(3.0), contains("възприема"));
  }

  private void initializeEvaluators(Language language) {
    vowelCount = new ConsecutiveVowelCount(LetterType.VOWELS, language);
    consonantCount = new ConsecutiveVowelCount(LetterType.CONSONANTS, language);
  }
}
