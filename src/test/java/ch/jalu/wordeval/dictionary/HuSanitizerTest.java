package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.AppData;
import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.evaluation.PartWordEvaluator;
import lombok.extern.log4j.Log4j2;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Test for the {@link HuSanitizer Hungarian dictionary} (which has custom sanitation).
 */
@Log4j2
public class HuSanitizerTest {
  
  
  @BeforeClass
  public static void initData() {
    AppData.init();
  }

  @Test
  public void shouldFindTheGivenWords() throws IOException {
    Dictionary dictionary = Dictionary.getDictionary("hu");
    if (!TestUtil.doesDictionaryFileExist(dictionary)) {
      log.warn("Skipping Hu sanitizer test because dictionary doesn't exist");
      return;
    }
    
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

    dictionary.process(evaluatorList);

    for (Evaluator<?> evaluator : evaluatorList) {
      MissingWordsEvaluator testEvaluator = (MissingWordsEvaluator) evaluator;
      List<String> missingWords = testEvaluator.getMissingWords();
      if (!missingWords.isEmpty()) {
        fail("Failed: Evaluator " + testEvaluator.intention
            + ". Missing words: " + missingWords);
      }
    }
  }

  private static class MissingWordsEvaluator extends PartWordEvaluator {

    private List<String> missingWords;
    // It should...
    final String intention;

    public MissingWordsEvaluator(String intention, String[] words) {
      this.intention = intention;
      this.missingWords = new ArrayList<>(Arrays.asList(words));
    }

    @Override
    public void processWord(String word, String rawWord) {
      if (missingWords.contains(word)) {
        missingWords.remove(word);
      }
    }

    public List<String> getMissingWords() {
      return missingWords;
    }
  }

}
