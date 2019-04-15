package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.jalu.wordeval.TestUtil.asSet;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link FullPalindromes}.
 */
public class FullPalindromesTest {

  private FullPalindromes evaluator = new FullPalindromes();

  @Test
  public void shouldFindFullPalindromes() {
    // given
    TreeMultimap<String, String> result = TreeMultimap.create();
    result.putAll("atta", asSet("attack", "attacked", "battalion"));
    result.putAll("ette", asSet("better", "letter"));
    result.putAll("eve", asSet("eve", "steve"));
    result.putAll("lagerregal", asSet("lagerregal"));

    // when
    ImmutableList<WordWithScore> results = EvaluatorTestHelper.evaluatePostEvaluatorWithResults(evaluator, createResultFromPalindromesEvaluator());

    // then
    Map<Double, List<String>> wordsByScore = results.stream() // todo extract
      .collect(Collectors.groupingBy(WordWithScore::getScore, Collectors.mapping(wws -> wws.getWord().getRaw(), Collectors.toList())));
    assertThat(wordsByScore, aMapWithSize(2));
    assertThat(wordsByScore.get(3.0), containsInAnyOrder("eve"));
    assertThat(wordsByScore.get(10.0), containsInAnyOrder("lagerregal"));
  }

  private static List<WordWithKey> createResultFromPalindromesEvaluator() {
    Multimap<String, String> result = HashMultimap.create();
    result.putAll("atta", asSet("attack", "attacked", "battalion"));
    result.putAll("ette", asSet("better", "letter"));
    result.putAll("eve", asSet("eve", "steve"));
    result.putAll("lagerregal", asSet("lagerregal"));

    List<WordWithKey> wordsWithKey = new ArrayList<>();
    for (Map.Entry<String, Collection<String>> entry : result.asMap().entrySet()) {
      String key = entry.getKey();
      for (String word : entry.getValue()) {
        wordsWithKey.add(new WordWithKey(new TestWord(word), key));
      }
    }
    return ImmutableList.copyOf(wordsWithKey);
  }

}
