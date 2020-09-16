package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithKeyAndScore;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;

/**
 * Test for {@link RepeatedSegmentConsecutive}.
 */
class RepeatedSegmentConsecutiveTest {

  private RepeatedSegmentConsecutive evaluator = new RepeatedSegmentConsecutive();

  @Test
  void shouldAddResults() {
    // given
    ImmutableList<WordWithKeyAndScore> repeatedSegmentResult = createRepeatedSegmentResult();

    // when
    ImmutableList<WordWithKey> results = EvaluatorTestHelper.evaluatePostEvaluatorWithResults(evaluator, repeatedSegmentResult);

    // then
    Map<String, List<String>> wordsByKey = results.stream()
      .collect(Collectors.groupingBy(WordWithKey::getKey,
        Collectors.mapping(wwk -> wwk.getWord().getRaw(), Collectors.toList())));
    assertThat(wordsByKey, aMapWithSize(9));
    assertThat(wordsByKey.get("elijkelijk"), contains("gelijkelijk"));
    assertThat(wordsByKey.get("anan"), contains("banana"));
    assertThat(wordsByKey.get("nana"), contains("banana"));
    assertThat(wordsByKey.get("rentrent"), contains("rentrent"));
    assertThat(wordsByKey.get("erer"), contains("Wanderer"));
    assertThat(wordsByKey.get("bebe"), contains("bebendo"));
    assertThat(wordsByKey.get("lalala"), contains("lalaBlalala"));
    assertThat(wordsByKey, not(hasKey("lala")));
    assertThat(wordsByKey.get("barbar"), containsInAnyOrder("barbarian", "barbarians"));
  }

  private static ImmutableList<WordWithKeyAndScore> createRepeatedSegmentResult() {
    // Key and score are irrelevant to the post evaluator
    return ImmutableList.<WordWithKeyAndScore>builder()
      .add(new WordWithKeyAndScore(new TestWord("gelijkelijk"), "foo", 3)) // elijkelijk
      .add(new WordWithKeyAndScore(new TestWord("banana"), "foo", 3))      // anan, nana
      .add(new WordWithKeyAndScore(new TestWord("nothing"), "foo", 3))     // --
      .add(new WordWithKeyAndScore(new TestWord("rentrent"), "foo", 3))    // rentrent
      .add(new WordWithKeyAndScore(new TestWord("Wanderer"), "foo", 3))    // erer
      .add(new WordWithKeyAndScore(new TestWord("bebendo"), "foo", 3))     // bebe
      .add(new WordWithKeyAndScore(new TestWord("lalaBlalala"), "foo", 3)) // lalala
      .add(new WordWithKeyAndScore(new TestWord("barbarian"), "foo", 3))   // barbar
      .add(new WordWithKeyAndScore(new TestWord("barbarians"), "foo", 3))  // barbar
      .build();
  }
}