package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import ch.jalu.wordeval.evaluators.result.WordGroupWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.language.Language;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link AllVowelsAlphabetically}.
 */
public class AllVowelsAlphabeticallyTest {

  @Test
  public void shouldFindMatches() {
    // given
    Language lang = newLanguage("zxx").build();
    AllVowelsAlphabetically allVowelsAlphabetically = new AllVowelsAlphabetically(lang);

    // when
    ImmutableList<WordGroupWithKey> results = EvaluatorTestHelper.evaluatePostEvaluatorWithResults(allVowelsAlphabetically, createVowelCountResults());

    // then
    Map<String, List<String>> groups = results.stream() // TODO: extract
      .collect(Collectors.toMap(WordGroupWithKey::getKey,
        group -> group.getWords().stream().map(Word::getRaw).collect(Collectors.toList())));
    assertThat(groups, aMapWithSize(2));
    assertThat(groups.get("aeiou"), containsInAnyOrder("arbeidsonrust", "marketingproduct"));
    assertThat(groups.get("aei"), contains("aei"));
  }

  private static List<WordWithKey> createVowelCountResults() {
    Multimap<String, String> results = HashMultimap.create();
    results.put("aeiou", "arbeidsonrust");
    results.put("aei", "aei");
    results.put("aeiou", "aarbeidsonrust");
    results.put("e", "test");
    results.put("aeiou", "arbiedsonrust");
    results.put("aeiou", "marketingproduct");

    return results.entries().stream()
      .map(e -> new WordWithKey(new TestWord(e.getValue()), e.getKey()))
      .collect(Collectors.toList());
  }
}