package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.oneOf;

/**
 * Test for {@link Evaluator}.
 */
class EvaluatorTest {

  /**
   * The evaluator should not add results that just differ in uppercase / lowercase.
   * It should prefer all-lowercase entries.
   */
  @Test
  void shouldHaveCaseInsensitiveResults() {
    String[] words = { "test", "Cup", "Test", "Word", "HELLO", "CUP", "hello", "word", "Hello" };
    TestEvaluator evaluator = new TestEvaluator();
    
    TestUtil.processWords(evaluator, words);
    evaluator.filterDuplicateWords(new Locale("en"));

    Multimap<Integer, String> results = evaluator.getResults();
    assertThat(results.keySet(), hasSize(3));
    // no guaranteed order in Set<> and we just take whichever word came first if it's not all lower-case
    assertThat(results.get(3), contains(oneOf("CUP", "Cup")));
    assertThat(results.get(4), containsInAnyOrder("test", "word"));
    assertThat(results.get(5), contains("hello"));
  }

  private static class TestEvaluator extends DictionaryEvaluator<Integer> {
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
