package ch.ljacqu.wordeval.evaluation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import ch.ljacqu.wordeval.TestUtil;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;

public class EvaluatorTest {

  /**
   * The evaluator should not add results that just differ in uppercase / lowercase.
   * It should prefer all-lowercase entries.
   */
  @Test
  public void shouldHaveCaseInsensitiveResults() {
    String[] words = { "test", "Cup", "Test", "Word", "HELLO", "CUP", "hello", "word", "Hello" };
    TestEvaluator evaluator = new TestEvaluator();
    
    TestUtil.processWords(evaluator, words);
    
    Map<Integer, Set<String>> results = evaluator.getResults();
    assertThat(results, aMapWithSize(3));
    assertThat(results.get(3), contains("Cup"));
    assertThat(results.get(4), containsInAnyOrder("test", "word"));
    assertThat(results.get(5), contains("hello"));
  }
  
  

  private static class TestEvaluator extends Evaluator<Integer> {
    @Override
    public void processWord(String word, String rawWord) {
      addEntry(word.length(), rawWord);
    }

    @Override
    protected ExportObject toExportObject(String identifier, ExportParams params) {
      return null;
    }
  }

}
