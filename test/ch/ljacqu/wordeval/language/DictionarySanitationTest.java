package ch.ljacqu.wordeval.language;

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
import ch.ljacqu.wordeval.LetterService;
import ch.ljacqu.wordeval.LetterType;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.PartWordEvaluator;

public class DictionarySanitationTest {

  private static final String languageCode = "af";

  @Test
  public void shouldNotHaveNonWordCharacters() throws IOException {
    List<Character> allowedChars = new ArrayList<>();
    allowedChars.add('-');
    allowedChars.add('\'');
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
      for (Map.Entry<String, List<String>> entry : errors.entrySet()) {
        System.err.println(entry.getKey() + ": " + entry.getValue());
      }
      fail("Found errors");
    } else {
      assertTrue(errors.isEmpty());
    }
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
      return WordForm.NO_ACCENTS;
    }
  }
}
