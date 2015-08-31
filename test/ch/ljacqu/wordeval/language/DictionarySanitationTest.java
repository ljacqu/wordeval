package ch.ljacqu.wordeval.language;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import ch.ljacqu.wordeval.LetterService;
import ch.ljacqu.wordeval.LetterType;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.PartWordEvaluator;

public class DictionarySanitationTest {

  private static final String languageCode = "hu";

  @Test
  public void shouldNotHaveForbiddenChars() throws IOException {
    ForbiddenCharsEvaluator testEvaluator = new ForbiddenCharsEvaluator();
    List<Evaluator> evaluators = Collections.singletonList(testEvaluator);
    Dictionary dictionary = Dictionary.getLanguageDictionary(languageCode,
        evaluators);

    dictionary.processDictionary();
    List<String> errors = testEvaluator.getErrors();

    if (!errors.isEmpty()) {
      fail("Found " + errors.size() + " errors: " + errors);
    }
    assertTrue(errors.isEmpty());
  }

  @Test
  @Ignore
  // TODO #20: Make test work with according sanitation
  public void shouldNotHaveNonWordCharacters() throws IOException {
    List<Character> allowedChars = new ArrayList<>();
    allowedChars.add('-');
    allowedChars.addAll(LetterService.getLetters(LetterType.VOWELS));
    allowedChars.addAll(LetterService.getLetters(LetterType.CONSONANTS));
    NoOtherCharsEvaluator testEvaluator = new NoOtherCharsEvaluator(
        allowedChars);

    List<Evaluator> evaluators = Collections.singletonList(testEvaluator);
    Dictionary dictionary = Dictionary.getLanguageDictionary(languageCode,
        evaluators);

    dictionary.processDictionary();
    Map<String, List<String>> errors = testEvaluator.getResults();

    if (!errors.isEmpty()) {
      testEvaluator.outputAggregatedResult();
      fail("Found errors");
    } else {
      assertTrue(errors.isEmpty());
    }
  }

  private static class ForbiddenCharsEvaluator extends PartWordEvaluator {
    private static final char[] ILLEGAL_CHARACTERS = { '/', ',', '\n', '(',
        ')', '[', ']', ' ', '!', '?', '_', '0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9' };

    private List<String> errors = new ArrayList<>();

    @Override
    public void processWord(String word, String rawWord) {
      for (char disallowedChar : ILLEGAL_CHARACTERS) {
        if (word.indexOf(disallowedChar) != -1) {
          errors.add(word + " has disallowed char '" + disallowedChar + "'");
        }
      }
    }

    public List<String> getErrors() {
      return errors;
    }
  }

  private static class NoOtherCharsEvaluator extends PartWordEvaluator {
    private final List<Character> allowedChars;

    public NoOtherCharsEvaluator(List<Character> allowedChars) {
      this.allowedChars = allowedChars;
    }

    @Override
    public void processWord(String word, String rawWord) {
      for (int i = 0; i < word.length(); ++i) {
        char curChar = word.charAt(i);
        if (!allowedChars.contains(curChar)) {
          addEntry(new Character(curChar).toString(), word);
        }
      }
    }

    @Override
    public WordForm getWordForm() {
      return WordForm.NO_ACCENTS;
    }
  }
}
