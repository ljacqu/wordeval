package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for {@link Emordnilap}.
 */
class EmordnilapTest {

  private Emordnilap emordnilap = new Emordnilap();

  @Test
  void shouldFindBackwardsPairs() {
    // given
    String[] words = new String[]{ "but", "parts", "potato", "strap", "tub", "working" };

    // when
    List<Set<String>> results = EvaluatorTestHelper.evaluateAndUnwrapWordGroups(emordnilap, words);

    // then
    assertThat(results, hasSize(2));
    assertThat(results.get(0), containsInAnyOrder("but", "tub"));
    assertThat(results.get(1), containsInAnyOrder("parts", "strap"));
  }

  @Test
  void shouldNotAddPalindromes() {
    // given
    String[] words = new String[]{"net", "otto", "Redder", "redder", "ten"};

    // when
    List<Set<String>> results = EvaluatorTestHelper.evaluateAndUnwrapWordGroups(emordnilap, words);

    // then
    assertThat(results, hasSize(1));
    assertThat(results.get(0), containsInAnyOrder("net", "ten"));
  }
}
