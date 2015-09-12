package ch.ljacqu.wordeval.extra;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import ch.ljacqu.wordeval.dictionary.Dictionary;
import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.PartWordEvaluator;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LetterService;
import ch.ljacqu.wordeval.language.LetterType;

/**
 * Utility test to verify how well a dictionary is being sanitized.
 */
public class DictionarySanitationTest {

  private static final String languageCode = "af";

  @Test
  public void shouldNotHaveNonWordCharacters() throws IOException {
    List<Character> allowedChars = computeAllowedCharsList();
    NoOtherCharsEvaluator testEvaluator = new NoOtherCharsEvaluator(
        allowedChars);

    List<Evaluator> evaluators = Collections.singletonList(testEvaluator);
    Dictionary dictionary = Dictionary.getDictionary(languageCode, evaluators);

    dictionary.process();
    Map<String, List<String>> errors = testEvaluator.getNavigableResults();

    if (!errors.isEmpty()) {
      for (Map.Entry<String, List<String>> entry : errors.entrySet()) {
        System.err.println(entry.getKey() + ": " + entry.getValue());
      }
      fail("Found errors");
    } else {
      assertTrue(errors.isEmpty());
    }
  }

  private List<Character> computeAllowedCharsList() {
    Language lang = Language.get(languageCode);
    List<Character> allowedChars = new ArrayList<>();
    for (String entry : LetterService.getLetters(LetterType.VOWELS, lang)) {
      if (entry.length() == 1) {
        allowedChars.add(entry.charAt(0));
      }
    }
    for (String entry : LetterService.getLetters(LetterType.CONSONANTS, lang)) {
      if (entry.length() == 1) {
        allowedChars.add(entry.charAt(0));
      }
    }
    return allowedChars;
  }

  private static class NoOtherCharsEvaluator extends PartWordEvaluator {
    private final char[] allowedChars;

    public NoOtherCharsEvaluator(List<Character> allowedChars) {
      Character[] allowedCharsArray = allowedChars
          .toArray(new Character[allowedChars.size()]);
      this.allowedChars = ArrayUtils.toPrimitive(allowedCharsArray);
    }

    @Override
    public void processWord(String word, String rawWord) {
      if (!StringUtils.containsOnly(word, allowedChars)) {
        int subIndex = StringUtils.indexOfAnyBut(word, allowedChars);
        addEntry(word.substring(subIndex, subIndex + 1), word);
      }
    }

    @Override
    public WordForm getWordForm() {
      return WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
    }
  }
}
