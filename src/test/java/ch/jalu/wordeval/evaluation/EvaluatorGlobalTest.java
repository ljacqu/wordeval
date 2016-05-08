package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import lombok.extern.log4j.Log4j2;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
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
@SuppressWarnings("JavaDoc")
public class EvaluatorGlobalTest {

  private static List<Evaluator<?>> evaluators;
  
  @BeforeClass
  public static void initializeEvaluators() {
    Language lang = newLanguage("zxx");
    evaluators = Arrays.asList(
        new AllVowels(LetterType.VOWELS),
        new AlphabeticalOrder(),
        new AlphabeticalSequence(),
        new Anagrams(),
        new BackwardsPairs(),
        new ConsecutiveLetterPairs(),
        new ConsecutiveVowelCount(LetterType.VOWELS, lang),
        new DiacriticHomonyms(lang.getLocale()),
        new FullPalindromes(),
        new Isograms(),
        new LongWords(),
        new Palindromes(),
        new SameLetterConsecutive(),
        new SingleVowel(LetterType.VOWELS),
        new VowelCount(LetterType.VOWELS, lang),
        new WordCollector());
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

}
