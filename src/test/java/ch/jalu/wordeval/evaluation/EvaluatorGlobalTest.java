package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.runners.EvaluatorInitializer;
import lombok.extern.log4j.Log4j2;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests common functionality of the evaluators.
 */
@Log4j2
public class EvaluatorGlobalTest {

  private static List<Evaluator<?>> evaluators;
  
  @BeforeClass
  public static void initializeEvaluators() {
    Language lang = newLanguage("zxx");
    evaluators = new EvaluatorInitializer(lang).getEvaluators();
    if (evaluators.isEmpty()) {
      throw new IllegalStateException("Could not instantiate evaluators");
    }
  }
  
  @Test
  public void shouldAllReturnWordForm() {
    evaluators.stream()
      .filter(e -> e instanceof DictionaryEvaluator)
      .map(e -> ((DictionaryEvaluator) e).getWordForm())
      .forEach(wordForm -> assertThat(wordForm, not(nullValue())));
  }
  
  @Test
  public void shouldConvertToExportObjectOrNull() {
    for (Evaluator evaluator : evaluators) {
      ExportObject exportObj = evaluator.toExportObject();
      if (exportObj == null) {
        log.info("Evaluator {} has null as export object", evaluator.getClass().getSimpleName());
      } else {
        assertThat(exportObj.getTopEntries(), anEmptyMap());
        assertThat(exportObj.getAggregatedEntries(), anEmptyMap());
      }      
    }
  }

  @Test
  public void shouldReturnBaseClassIfPostEvaluator() {
    evaluators.stream()
        .filter(e -> e instanceof PostEvaluator)
        .map(PostEvaluator.class::cast)
        .forEach(postEvaluator -> assertThat(postEvaluator.getType(), not(nullValue())));
  }

}
