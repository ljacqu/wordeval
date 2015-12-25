package ch.ljacqu.wordeval.evaluation;

import static ch.ljacqu.wordeval.TestUtil.asSet;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;

public class FullPalindromesTest {

  @Test
  public void shouldFindFullPalindromes() {
    Map<String, Set<String>> result = new HashMap<>();
    result.put("atta", asSet("attack", "attacked", "battalion"));
    result.put("ette", asSet("better", "letter"));
    result.put("eve", asSet("eve", "steve"));
    result.put("lagerregal", asSet("lagerregal"));

    Palindromes palindromes = Mockito.mock(Palindromes.class);
    when(palindromes.getResults()).thenReturn(result);

    FullPalindromes fullPalindromes = new FullPalindromes();
    fullPalindromes.evaluateWith(palindromes);

    Map<Integer, Set<String>> fullResults = fullPalindromes.getResults();
    assertThat(fullResults, aMapWithSize(2));
    assertThat(fullResults.get(3), containsInAnyOrder("eve"));
    assertThat(fullResults.get(10), containsInAnyOrder("lagerregal"));
  }

}
