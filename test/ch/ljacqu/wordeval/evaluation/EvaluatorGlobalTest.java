package ch.ljacqu.wordeval.evaluation;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.ljacqu.wordeval.TestUtil.ListInit;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.PartWordExport;
import ch.ljacqu.wordeval.evaluation.export.WordStatExport;
import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LetterType;
import lombok.extern.log4j.Log4j2;

/**
 * Tests common functionality of the evaluators.
 */
@Log4j2
public class EvaluatorGlobalTest {

  private static List<Evaluator<?>> evaluators;
  
  @BeforeClass
  @SuppressWarnings("unchecked")
  public static void initializeEvaluators() {
    Language lang = new Language("zxx", Alphabet.LATIN);
    evaluators = ListInit
        .init(new AllVowels(LetterType.VOWELS))
        .add(new AlphabeticalOrder())
        .add(new AlphabeticalSequence())
        .add(new Anagrams())
        .add(new BackwardsPairs())
        .add(new ConsecutiveLetterPairs())
        .add(new DiacriticHomonyms(lang))
        .add(new FullPalindromes())
        .add(new Isograms())
        .add(new LongWords())
        .add(new Palindromes())
        .add(new SameLetterConsecutive())
        .add(new SingleVowel(LetterType.VOWELS))
        .add(new VowelCount(LetterType.VOWELS, lang))
        .add(new WordCollector())
        .getList();
  }
  
  @Test
  public void shouldAllReturnWordForm() {
    evaluators.stream()
      .map(Evaluator::getWordForm)
      .forEach(wordForm -> assertThat(wordForm, not(nullValue())));
  }
  
  @Test
  public void shouldConvertToExportObjectOrNull() {
    for (Evaluator evaluator : evaluators) {
      ExportObject exportObj = evaluator.toExportObject();
      if (exportObj == null) {
        log.info("Evaluator {} has null as export object", evaluator.getClass().getSimpleName());
      } else {
        validateEmpty(exportObj);
      }      
    }
  }
  
  @Test
  public void shouldHaveProcessWordForPostEvaluators() {
    evaluators.stream()
      .filter(evaluator -> {
        return Arrays.stream(evaluator.getClass().getMethods())
          .filter(method -> method.isAnnotationPresent(PostEvaluator.class))
          .findAny()
          .isPresent();
      })
      .forEach(evaluator -> evaluator.processWord("", ""));
  }

  private static void validateEmpty(ExportObject eo) {
    if (eo instanceof PartWordExport) {
      assertThat(((PartWordExport) eo).getTopEntries(), anEmptyMap());
      assertThat(((PartWordExport) eo).getAggregatedEntries(), anEmptyMap());
    } else if (eo instanceof WordStatExport) {
      assertThat(((WordStatExport) eo).getTopEntries(), anEmptyMap());
      assertThat(((WordStatExport) eo).getAggregatedEntries(), anEmptyMap());
    } else {
      throw new IllegalStateException("Unknown ExportObject type " + eo);
    }
  }
}
