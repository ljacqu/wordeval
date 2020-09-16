package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link AllVowels}.
 */
class AllVowelsTest {

  @Test
  void shouldFindWordsWithAllVowels() {
    // given
    AllVowels evaluator = new AllVowels(LetterType.VOWELS);

    // when
    ImmutableList<WordWithKey> results = EvaluatorTestHelper.evaluatePostEvaluatorWithResults(evaluator, createVowelCountResults());

    // then
    Map<String, List<String>> wordsByKey = results.stream() // todo extract
      .collect(Collectors.groupingBy(WordWithKey::getKey,
        Collectors.mapping(wwk -> wwk.getWord().getRaw(), Collectors.toList())));
    assertThat(wordsByKey.keySet(), containsInAnyOrder("aeiou", "eiou", "aeiu", "ae"));
    assertThat(wordsByKey.get("aeiou"), containsInAnyOrder("sequoia", "miscellaneous", "simultaneous"));
    assertThat(wordsByKey.get("eiou"), containsInAnyOrder("question", "questions", "questioning"));
  }

  private static List<WordWithKey> createVowelCountResults() {
    Multimap<String, String> results = HashMultimap.create();
    results.putAll("ae", Set.of("bear", "care"));
    results.putAll("aeiu", Set.of("beautiful"));
    results.putAll("eiou", Set.of("question", "questions", "questioning"));
    results.putAll("aeiou", Set.of("sequoia", "miscellaneous", "simultaneous"));

    return results.entries().stream()
      .map(entry -> new WordWithKey(new TestWord(entry.getValue()), entry.getKey()))
      .collect(Collectors.toList());
  }

}
