package ch.jalu.wordeval.evaluation;

import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

/**
 * Test for {@link Emordnilap}.
 */
class EmordnilapTest {
  
  @Test
  void shouldFindBackwardsPairs() {
    String[] words = new String[]{ "but", "parts", "potato", "strap", "tub", "working" };
    WordCollector collector = setUpCollector(words);
    
    Emordnilap evaluator = new Emordnilap();
    evaluator.evaluateWith(collector);
    Multimap<String, String> results = evaluator.getResults();
    
    assertThat(results.keySet(), containsInAnyOrder("but", "parts"));
    assertThat(results.get("but"), contains("tub"));
    assertThat(results.get("parts"), contains("strap"));
  }
  
  @Test
  void shouldNotAddPalindromes() {
    WordCollector collector = setUpCollector("net", "otto", "Redder", "redder", "ten");
    
    Emordnilap evaluator = new Emordnilap();
    evaluator.evaluateWith(collector);
    Multimap<String, String> results = evaluator.getResults();
    
    assertThat(results.keySet(), contains("net"));
  }
  
  @Test
  void shouldBeCaseInsensitive() {
    WordCollector collector = setUpCollector("BUT", "Net", "parts", "Strap", "TEN", "tub");
    
    Emordnilap evaluator = new Emordnilap();
    evaluator.evaluateWith(collector);
    Multimap<String, String> results = evaluator.getResults();
    
    assertThat(results.keySet(), containsInAnyOrder("but", "net", "parts"));
  }
  
  private WordCollector setUpCollector(String... words) {
    WordCollector collector = Mockito.mock(WordCollector.class);
    when(collector.returnSortedWords())
        .thenReturn(Arrays.asList(words));
    return collector;
  }

}
