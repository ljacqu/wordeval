package ch.ljacqu.wordeval.language;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.PartWordEvaluator;

public class UtilDoesWordExist {

  private static final String languageCode = "hu";

  @Ignore
  @Test
  public void shouldNotReturnNonWordChars() throws IOException {
    TestEvaluator testEvaluator = new TestEvaluator();
    List<Evaluator> evaluators = Collections.singletonList(testEvaluator);
    Dictionary dictionary = Dictionary.getLanguageDictionary(languageCode,
        evaluators);

    dictionary.processDictionary();

    List<String> missingWords = testEvaluator.getMissingWords();
    if (missingWords.isEmpty()) {
      System.out.println("Success -- found all words");
    } else {
      System.out.println("Words missing: " + missingWords);
    }
  }

  private static class TestEvaluator extends PartWordEvaluator {

    public static final String[] WORDS_TO_FIND = { 
      "üzembe", "helyezés", "is", "bogusWord"
    };

    private List<String> missingWords = new ArrayList<String>(
        Arrays.asList(WORDS_TO_FIND));

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
