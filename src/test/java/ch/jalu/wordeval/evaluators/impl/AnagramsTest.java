package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.dictionary.WordFactory;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link Anagrams}.
 */
class AnagramsTest extends AbstractEvaluatorTest {

  private final Anagrams anagrams = new Anagrams();

  @Test
  void shouldFindAnagrams() {
    // given
    // {race, care, acre}, {tea, eat}, {fro, for}, a, something, test
    List<Word> words = createWords("race", "for", "a", "eat", "care", "something", "acre", "fro", "tea", "test", "test");

    // when
    anagrams.evaluate(words);

    // then
    Map<String, Set<String>> results = groupResultsByKey(anagrams.getResults());
    assertThat(results, aMapWithSize(3));
    assertThat(results.get("acer"), containsInAnyOrder("race", "care", "acre"));
    assertThat(results.get("aet"), containsInAnyOrder("tea", "eat"));
    assertThat(results.get("for"), containsInAnyOrder("for", "fro"));
  }

  @Test
  void shouldSkipCapitalizedDuplicate() {
    // given
    List<Word> words = createRealWords("aaa", "Lane", "tones", "Stone", "tone's", "stone", "Tones", "Lena", "lane");

    anagrams.evaluate(words);

    // then
    Map<String, Set<String>> results = groupResultsByKey(anagrams.getResults());
    assertThat(results.keySet(), contains("enost", "aeln"));
    assertThat(results.get("enost"), containsInAnyOrder("stone", "tone's", "tones"));
    assertThat(results.get("aeln"), containsInAnyOrder("lane", "Lena"));
  }

  /**
   * Creates real Word implementations ({@link ch.jalu.wordeval.dictionary.TestWord} uses the same text for all
   * word forms).
   *
   * @param words strings to build words from
   * @return the created words
   */
  private static List<Word> createRealWords(String... words) {
    Language language = Language.builder("en", "English", Alphabet.LATIN).build();
    WordFactory wordFactory = new WordFactory(language);

    return Arrays.stream(words)
        .map(wordFactory::createWordObject)
        .toList();
  }
}
