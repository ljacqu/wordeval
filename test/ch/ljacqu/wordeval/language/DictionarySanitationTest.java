package ch.ljacqu.wordeval.language;

import static org.junit.Assert.fail;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import ch.ljacqu.wordeval.evaluation.Evaluator;

public class DictionarySanitationTest {

  private String languageCode = "af";

  @SuppressWarnings("rawtypes")
  @Test
  public void shouldNotReturnNonWordChars() throws Exception {
    Evaluator testEvaluator = new TestEvaluator();
    List<Evaluator> evaluators = Collections.singletonList(testEvaluator);
    Dictionary dictionary = Dictionary.getLanguageDictionary(languageCode,
        evaluators);
    dictionary.processDictionary();
  }

  private class TestEvaluator extends Evaluator<String, String> {

    private final char[] disallowedChars = { '/', ',', '\n', '(', ')', '[', ']' };

    @Override
    public void processWord(String word, String rawWord) {
      for (char disallowedChar : disallowedChars) {
        if (word.indexOf(disallowedChar) != -1) {
          fail(word + " has disallowed char " + disallowedChar);
        }
      }
    }

  }
}
