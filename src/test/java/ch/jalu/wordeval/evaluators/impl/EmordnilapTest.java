package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;

/**
 * Test for {@link Emordnilap}.
 */
class EmordnilapTest extends AbstractEvaluatorTest {

  private final Emordnilap emordnilap = new Emordnilap();

  @Test
  void shouldFindBackwardsPairs() {
    // given
    List<Word> words = createWords("but", "parts", "potato", "strap", "tub", "working");

    // when
    emordnilap.evaluate(words);

    // then
    Map<String, Set<String>> results = groupByKey(emordnilap.getResults());
    assertThat(results, aMapWithSize(2));
    assertThat(results.get("but"), contains("tub"));
    assertThat(results.get("parts"), contains("strap"));
  }

  @Test
  void shouldNotAddPalindromes() {
    // given
    List<Word> words = createWords("net", "otto", "Redder", "redder", "ten");

    // when
    emordnilap.evaluate(words);

    // then
    Map<String, Set<String>> results = groupByKey(emordnilap.getResults());
    assertThat(results, aMapWithSize(1));
    assertThat(results.get("net"), contains("ten"));
  }
}
