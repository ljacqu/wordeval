package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.runners.EvaluatorInitializer;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests common functionality of the evaluators.
 */
@Log4j2
class EvaluatorGlobalTest {

  private static List<Evaluator<?>> evaluators;
  
  @BeforeAll
  static void initializeEvaluators() {
    Language lang = newLanguage("zxx").build();
    evaluators = new EvaluatorInitializer(lang).getEvaluators();
    if (evaluators.isEmpty()) {
      throw new IllegalStateException("Could not instantiate evaluators");
    }
  }
  
  @Test
  void shouldAllReturnWordForm() {
    evaluators.stream()
      .filter(e -> e instanceof DictionaryEvaluator)
      .map(e -> ((DictionaryEvaluator) e).getWordForm())
      .forEach(wordForm -> assertThat(wordForm, not(nullValue())));
  }
  
  @Test
  void shouldConvertToExportObjectOrNull() {
    for (Evaluator evaluator : evaluators) {
      ExportObject<?, ?, ?> exportObj = evaluator.toExportObject();
      if (exportObj == null) {
        log.info("Evaluator {} has null as export object", evaluator.getClass().getSimpleName());
      } else {
        assertThat(exportObj.getTopEntries(), anEmptyMap());
        assertThat(exportObj.getAggregatedEntries(), anEmptyMap());
      }      
    }
  }

  @Test
  void shouldReturnBaseClassIfPostEvaluator() {
    evaluators.stream()
        .filter(e -> e instanceof PostEvaluator)
        .map(PostEvaluator.class::cast)
        .forEach(postEvaluator -> assertThat(postEvaluator.getType(), not(nullValue())));
  }

}
