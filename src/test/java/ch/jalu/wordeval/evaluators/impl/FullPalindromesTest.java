package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.evaluators.processing.AllWordsEvaluatorProvider;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link FullPalindromes}.
 */
class FullPalindromesTest extends AbstractEvaluatorTest {

  private final FullPalindromes fullPalindromes = new FullPalindromes();

  @Test
  void shouldFindFullPalindromes() {
    // given
    AllWordsEvaluatorProvider evaluatorProvider = createProviderWithPalindromesResult();

    // when
    fullPalindromes.evaluate(evaluatorProvider);

    // then
    Map<Double, Set<String>> wordsByScore = groupByScore(fullPalindromes.getResults());
    assertThat(wordsByScore, aMapWithSize(2));
    assertThat(wordsByScore.get(3.0), containsInAnyOrder("eve"));
    assertThat(wordsByScore.get(10.0), containsInAnyOrder("lagerregal"));
  }

  private static AllWordsEvaluatorProvider createProviderWithPalindromesResult() {
    SetMultimap<String, String> result = HashMultimap.create();
    result.putAll("atta", Set.of("attack", "attacked", "battalion"));
    result.putAll("ette", Set.of("better", "letter"));
    result.putAll("eve", Set.of("eve", "steve"));
    result.putAll("lagerregal", Set.of("lagerregal"));

    List<WordWithKey> wordsWithKey = new ArrayList<>();
    for (Map.Entry<String, Set<String>> entry : Multimaps.asMap(result).entrySet()) {
      String key = entry.getKey();
      for (String word : entry.getValue()) {
        wordsWithKey.add(new WordWithKey(new TestWord(word), key));
      }
    }

    Palindromes palindromesEvaluator = mock(Palindromes.class);
    given(palindromesEvaluator.getResults()).willReturn(wordsWithKey);
    return new AllWordsEvaluatorProvider(List.of(palindromesEvaluator));
  }
}
