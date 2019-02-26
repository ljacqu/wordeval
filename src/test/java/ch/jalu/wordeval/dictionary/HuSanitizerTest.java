package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.dictionary.sanitizer.HuSanitizer;
import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.evaluation.PartWordEvaluator;
import ch.jalu.wordeval.runners.DictionaryProcessor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Test for the {@link HuSanitizer Hungarian dictionary} (which has custom sanitation).
 */
@Log4j2
public class HuSanitizerTest {

  private static Dictionary huDictionary;
  
  @BeforeClass
  public static void initData() {
    huDictionary = new AppData().getDictionary("hu");
  }

  @Test
  public void shouldFindTheGivenWords() {
    if (!TestUtil.doesDictionaryFileExist(huDictionary)) {
      log.warn("Skipping Hu sanitizer test because dictionary doesn't exist");
      return;
    }

    // given
    String[] words1 = { "csomóan", "csomó", "háromnegyed", "harmad",
        "kilenced", "milliomod", "trilliomod" };
    MissingWordsEvaluator evaluator1 = new MissingWordsEvaluator(
        "should receive the words between Roman numerals", words1);

    String[] words2 = { "alak", "kor", "közben", "módra", "szer", "vég",
        "vége", "végén", "végi", "vevő", "cél" };
    MissingWordsEvaluator evaluator2 = new MissingWordsEvaluator(
        "should receive the split second words in two-word entries", words2);

    String[] words3 = { "csak", "azért", "úti", "fő", "is" };
    MissingWordsEvaluator evaluator3 = new MissingWordsEvaluator(
        "should get other words that have special treatment", words3);

    List<Evaluator<?>> evaluatorList = Arrays.asList(evaluator1, evaluator2, evaluator3);

    // when
    DictionaryProcessor.process(huDictionary, evaluatorList);

    // then
    for (Evaluator<?> evaluator : evaluatorList) {
      MissingWordsEvaluator testEvaluator = (MissingWordsEvaluator) evaluator;
      List<String> missingWords = testEvaluator.getMissingWords();
      if (!missingWords.isEmpty()) {
        fail("Failed: Evaluator " + testEvaluator.getIntention()
            + ". Missing words: " + missingWords);
      }
    }
  }

  @Getter
  private static final class MissingWordsEvaluator extends PartWordEvaluator {

    private List<String> missingWords;
    // It should...
    private final String intention;

    MissingWordsEvaluator(String intention, String[] words) {
      this.intention = intention;
      this.missingWords = new ArrayList<>(Arrays.asList(words));
    }

    @Override
    public void processWord(String word, String rawWord) {
      missingWords.remove(word);
    }
  }

}
