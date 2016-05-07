package ch.jalu.wordeval.evaluation;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.junit.Test;
import org.mockito.Mockito;

import static ch.jalu.wordeval.TestUtil.asSet;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class FullPalindromesTest {

  @Test
  public void shouldFindFullPalindromes() {
    TreeMultimap<String, String> result = TreeMultimap.create();
    result.putAll("atta", asSet("attack", "attacked", "battalion"));
    result.putAll("ette", asSet("better", "letter"));
    result.putAll("eve", asSet("eve", "steve"));
    result.putAll("lagerregal", asSet("lagerregal"));

    Palindromes palindromes = Mockito.mock(Palindromes.class);
    when(palindromes.getResults()).thenReturn(result);

    FullPalindromes fullPalindromes = new FullPalindromes();
    fullPalindromes.evaluateWith(palindromes);

    Multimap<Integer, String> fullResults = fullPalindromes.getResults();
    assertThat(fullResults.keySet(), hasSize(2));
    assertThat(fullResults.get(3), containsInAnyOrder("eve"));
    assertThat(fullResults.get(10), containsInAnyOrder("lagerregal"));
  }

}
