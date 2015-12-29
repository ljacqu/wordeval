package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.TestUtil.ANewList;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.PartWordExport;
import ch.ljacqu.wordeval.evaluation.export.WordStatExport;
import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LetterType;
import lombok.extern.log4j.Log4j2;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static ch.ljacqu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests common functionality of the evaluators.
 */
@Log4j2
@SuppressWarnings("JavaDoc")
public class EvaluatorGlobalTest {

  private static List<Evaluator<?>> evaluators;
  
  @BeforeClass
  @SuppressWarnings("unchecked")
  public static void initializeEvaluators() {
    Language lang = newLanguage("zxx");
    evaluators = ANewList
        .with(new AllVowels(LetterType.VOWELS))
        .and(new AlphabeticalOrder())
        .and(new AlphabeticalSequence())
        .and(new Anagrams())
        .and(new BackwardsPairs())
        .and(new ConsecutiveLetterPairs())
        .and(new ConsecutiveVowelCount(LetterType.VOWELS, lang))
        .and(new DiacriticHomonyms(lang.getLocale()))
        .and(new FullPalindromes())
        .and(new Isograms())
        .and(new LongWords())
        .and(new Palindromes())
        .and(new SameLetterConsecutive())
        .and(new SingleVowel(LetterType.VOWELS))
        .and(new VowelCount(LetterType.VOWELS, lang))
        .and(new WordCollector())
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
