package ch.jalu.wordeval.evaluation;

import com.google.common.collect.Multimap;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class BackwardsPairsTest {
  
  @Test
  public void shouldFindBackwardsPairs() {
    String[] words = new String[]{ "but", "parts", "potato", "strap", "tub", "working" };
    WordCollector collector = setUpCollector(words);
    
    BackwardsPairs evaluator = new BackwardsPairs();
    evaluator.evaluateWith(collector);
    Multimap<String, String> results = evaluator.getResults();
    
    assertThat(results.keySet(), containsInAnyOrder("but", "parts"));
    assertThat(results.get("but"), contains("tub"));
    assertThat(results.get("parts"), contains("strap"));
  }
  
  @Test
  public void shouldNotAddPalindromes() {
    WordCollector collector = setUpCollector("net", "otto", "Redder", "redder", "ten");
    
    BackwardsPairs evaluator = new BackwardsPairs();
    evaluator.evaluateWith(collector);
    Multimap<String, String> results = evaluator.getResults();
    
    assertThat(results.keySet(), contains("net"));
  }
  
  @Test
  public void shouldBeCaseInsensitive() {
    WordCollector collector = setUpCollector("BUT", "Net", "parts", "Strap", "TEN", "tub");
    
    BackwardsPairs evaluator = new BackwardsPairs();
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