package ch.jalu.wordeval.extra;

import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.jalu.wordeval.AppData;
import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.evaluation.PartWordEvaluator;

/**
 * Utility test to verify if a certain word appears in a given dictionary.
 * (Useful to make sure a custom sanitizer is not too strict.)
 */
public class UtilDoesWordExist {

  private static final String LANGUAGE = "hu";
  
  private static final String[] WORDS_TO_FIND = { 
    "üzembe", "helyezés", "is", "bogusWord"
  };
  
  @BeforeClass
  public static void initData() {
    AppData.init();
  }

  @Ignore
  @Test
  public void shouldFindWords() {
    TestEvaluator testEvaluator = new TestEvaluator();
    List<Evaluator<?>> evaluators = Collections.singletonList(testEvaluator);
    Dictionary dictionary = Dictionary.getDictionary(LANGUAGE);

    dictionary.process(evaluators);

    List<String> missingWords = testEvaluator.getMissingWords();
    if (missingWords.isEmpty()) {
      System.out.println("Success -- found all words");
    } else {
      System.out.println("Words missing: " + missingWords);
    }
  }

  private static class TestEvaluator extends PartWordEvaluator {

    private List<String> missingWords = TestUtil.asList(WORDS_TO_FIND);

    @Override
    public void processWord(String word, String rawWord) {
      for (String wordToFind : WORDS_TO_FIND) {
        if (word.equals(wordToFind)) {
          missingWords.remove(wordToFind);
          System.out.println("Found word '" + wordToFind + "'");
        }
      }
    }

    public List<String> getMissingWords() {
      return missingWords;
    }
  }
}
