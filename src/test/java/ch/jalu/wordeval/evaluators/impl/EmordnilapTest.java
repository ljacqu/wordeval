package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Emordnilap}.
 */
public class EmordnilapTest {

  private Emordnilap emordnilap = new Emordnilap();

  @Test
  public void shouldFindBackwardsPairs() {
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
  public void shouldNotAddPalindromes() {
    // given
    String[] words = new String[]{"net", "otto", "Redder", "redder", "ten"};

    // when
    List<Set<String>> results = EvaluatorTestHelper.evaluateAndUnwrapWordGroups(emordnilap, words);

    // then
    assertThat(results, hasSize(1));
    assertThat(results.get(0), containsInAnyOrder("net", "ten"));
  }
}
