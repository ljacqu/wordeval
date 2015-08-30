package ch.ljacqu.wordeval.language;

import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.PartWordEvaluator;

public class DictionarySanitationTest {

  private static final String languageCode = "hu";

  @Test
  public void shouldNotReturnNonWordChars() throws IOException {
    TestEvaluator testEvaluator = new TestEvaluator();
    List<Evaluator> evaluators = Collections.singletonList(testEvaluator);
    Dictionary dictionary = Dictionary.getLanguageDictionary(languageCode,
        evaluators);

    dictionary.processDictionary();
    List<String> errors = testEvaluator.getErrors();

    if (!errors.isEmpty()) {
      fail("Found " + errors.size() + " errors: " + errors);
    }
  }

  private static class TestEvaluator extends PartWordEvaluator {

    private static final char[] ILLEGAL_CHARACTERS = { '/', ',', '\n', '(',
        ')', '[', ']', ' ', '!', '?', '_' };

    private List<String> errors = new ArrayList<String>();

    @Override
    public void processWord(String word, String rawWord) {
      for (char disallowedChar : ILLEGAL_CHARACTERS) {
        if (word.indexOf(disallowedChar) != -1) {
          errors.add(word + " has disallowed char '" + disallowedChar + "'");
        } else if (word.matches("\\d")) {
          errors.add(word + " contains a digit");
        }
      }
    }

    public List<String> getErrors() {
      return errors;
    }

  }
}
