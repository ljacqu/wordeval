package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.evaluation.DictionaryEvaluator;
import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.evaluation.PostEvaluator;
import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.language.Language;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@SuppressWarnings("javadoc")
public class DictionaryTest {

  private static Language zxxLanguage;
  
  @BeforeClass
  public static void setUpTestLanguage() {
    zxxLanguage = newLanguage("zxx");
    Language.add(zxxLanguage);
    DictionarySettings.add("zxx").setDelimiters('/');
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
    Evaluator<Boolean> lowercaseEval = new LowercaseEvaluator();
    Evaluator<Boolean> noAccentEval = new NoAccentEvaluator();
    Evaluator<Boolean> postEval = new TestPostEvaluator();
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
  
  private static class LowercaseEvaluator extends DictionaryEvaluator<Boolean> {
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
  
  private static class NoAccentEvaluator extends DictionaryEvaluator<Boolean> {
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
  
  public static class TestPostEvaluator extends PostEvaluator<Boolean, LowercaseEvaluator> {
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
