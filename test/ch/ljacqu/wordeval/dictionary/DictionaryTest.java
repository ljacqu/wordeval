package ch.ljacqu.wordeval.dictionary;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import ch.ljacqu.wordeval.evaluation.PostEvaluator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import ch.ljacqu.wordeval.DataUtils;
import ch.ljacqu.wordeval.TestUtil;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;
import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;

public class DictionaryTest {

  private static Language zxxLanguage;
  private static DictionarySettings zxxSettings;
  
  @BeforeClass
  public static void setUpTestLanguage() {
    zxxLanguage = new Language("zxx", Alphabet.LATIN);
    Language.add(zxxLanguage);
    zxxSettings = DictionarySettings.add("zxx").setDelimiters('/');
  }
  
  @Test
  public void shouldInitializeDictionaryWithCode() {
    Dictionary dict = Dictionary.getDictionary("zxx");
    Dictionary dict2 = Dictionary.getDictionary("zxx", "zxx", "test-file.dic");
    
    assertThat(dict.getLanguage(), equalTo(zxxLanguage));
    assertThat(dict2.getLanguage(), equalTo(zxxLanguage));
  }
  
  @Test
  public void shouldCallAllEvaluators() {
    Evaluator<?> lowercaseEval = new LowercaseEvaluator();
    Evaluator<?> noAccentEval = new NoAccentEvaluator();
    Evaluator<?> postEval = new TestPostEvaluator();
    List<Evaluator<?>> evaluators = Arrays.asList(lowercaseEval, noAccentEval, postEval);
    Dictionary dict = Dictionary.getDictionary("zxx");
    DataUtils dataUtils = Mockito.mock(DataUtils.class);
    when(dataUtils.readFileLines(anyString())).thenReturn(Arrays.asList("Some", "/a", "tëst", "Wôrds", "here/23"));
    TestUtil.R.setField(Dictionary.class, dict, "dataUtils", dataUtils);
    
    dict.process(evaluators);
    
    // This is more an integration test since we check for the correct generation of the word forms
    // and that a postevaluator is handled correctly, but it's the only way to guarantee that the
    // Dictionary instance really handles all of these tasks at the right places.
    assertThat(lowercaseEval.getResults().get(Boolean.TRUE), 
        containsInAnyOrder("some", "tëst", "wôrds", "here"));
    assertThat(noAccentEval.getResults().get(Boolean.TRUE), containsInAnyOrder("some", "test", "words", "here"));
    assertThat(postEval.getResults().get(Boolean.TRUE), hasSize(1));
    String lowercaseResultSize = Integer.toString(lowercaseEval.getResults().size());
    assertTrue(postEval.getResults().get(Boolean.TRUE).contains(lowercaseResultSize));
  }

  private static class TestSanitizer extends Sanitizer {
    public TestSanitizer() {
      super(zxxSettings);
    }
  }
  
  private static class LowercaseEvaluator extends Evaluator<Boolean> {
    @Override
    public void processWord(String word, String rawWord) {
      addEntry(Boolean.TRUE, word);
    }

    @Override
    protected ExportObject toExportObject(String identifier, ExportParams params) {
      return null;
    }
    
    @Override
    public WordForm getWordForm() {
      return WordForm.LOWERCASE;
    }
  }
  
  private static class NoAccentEvaluator extends Evaluator<Boolean> {
    @Override
    public void processWord(String word, String rawWord) {
      addEntry(Boolean.TRUE, word);
    }
    
    @Override
    protected ExportObject toExportObject(String id, ExportParams params) {
      return null;
    }
    
    @Override
    public WordForm getWordForm() {
      return WordForm.NO_ACCENTS;
    }
  }
  
  public static class TestPostEvaluator extends Evaluator<Boolean> implements PostEvaluator<LowercaseEvaluator> {
    @Override
    public void processWord(String word, String rawWord) {
      // --
    }
    
    @Override
    public ExportObject toExportObject(String id, ExportParams params) {
      return null;
    }
    
    @Override
    public void evaluateWith(LowercaseEvaluator base) {
      addEntry(Boolean.TRUE, Integer.toString(base.getResults().size()));
    }
    @Override public Class<LowercaseEvaluator> getType() { return LowercaseEvaluator.class; }
  }
  
  

}
