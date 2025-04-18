package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.evaluators.processing.AllWordsEvaluatorProvider;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link AllVowelsAlphabetically}.
 */
class AllVowelsAlphabeticallyTest extends AbstractEvaluatorTest {

  @Test
  void shouldFindMatches() {
    // given
    Language lang = newLanguage("zxx").build();
    AllVowelsAlphabetically allVowelsAlphabetically = new AllVowelsAlphabetically(lang);
    AllWordsEvaluatorProvider allWordsEvaluatorProvider = createProviderWithVowelCountResults();

    // when
    allVowelsAlphabetically.evaluate(allWordsEvaluatorProvider);

    // then
    Map<String, Set<String>> results = groupResultsByKey(allVowelsAlphabetically.getResults());
    assertThat(results, aMapWithSize(2));
    assertThat(results.get("aeiou"), containsInAnyOrder("arbeidsonrust", "marketingproduct"));
    assertThat(results.get("aei"), contains("aei"));
  }

  private static AllWordsEvaluatorProvider createProviderWithVowelCountResults() {
    Multimap<String, String> results = HashMultimap.create();
    results.put("aeiou", "arbeidsonrust");
    results.put("aei", "aei");
    results.put("aeiou", "aarbeidsonrust");
    results.put("e", "test");
    results.put("aeiou", "arbiedsonrust");
    results.put("aeiou", "marketingproduct");

    List<WordWithKey> vowelCountResults = results.entries().stream()
      .map(e -> new WordWithKey(new TestWord(e.getValue()), e.getKey()))
      .collect(Collectors.toList());

    VowelCount vowelCount = mock(VowelCount.class);
    given(vowelCount.getResults()).willReturn(vowelCountResults);
    given(vowelCount.getLetterType()).willReturn(LetterType.VOWELS);
    return new AllWordsEvaluatorProvider(List.of(vowelCount));
  }
}