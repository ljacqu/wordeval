package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link AllVowels}.
 */
public class AllVowelsTest {

  @Test
  public void shouldFindWordsWithAllVowels() {
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
    results.putAll("ae", TestUtil.asSet("bear", "care"));
    results.putAll("aeiu", TestUtil.asSet("beautiful"));
    results.putAll("eiou", TestUtil.asSet("question", "questions", "questioning"));
    results.putAll("aeiou", TestUtil.asSet("sequoia", "miscellaneous", "simultaneous"));

    return results.entries().stream()
      .map(entry -> new WordWithKey(new TestWord(entry.getValue()), entry.getKey()))
      .collect(Collectors.toList());
  }

}
