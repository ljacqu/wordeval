package ch.ljacqu.wordeval.extra;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import ch.ljacqu.wordeval.AppData;
import ch.ljacqu.wordeval.dictionary.Dictionary;
import ch.ljacqu.wordeval.dictionary.DictionarySettings;
import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.PartWordEvaluator;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LanguageService;
import ch.ljacqu.wordeval.language.LetterType;

/**
 * Utility test to verify how well a dictionary is being sanitized.
 */
public class DictionarySanitationTest {
  
  static {
    AppData.init();
  }
  
  @Test
  @Ignore
  public void shouldSanitizeDictionaries() {
    Map<String, Map<String, Set<String>>> sanitationResult = new HashMap<>();
    Iterable<String> languageCodes = DictionarySettings.getAllCodes();
    
    for (String languageCode : languageCodes) {
      sanitationResult.put(languageCode, findBadWords(languageCode));
    }
    
    boolean isEmpty = true;
    for (Map<String, Set<String>> entry : sanitationResult.values()) {
      if (!entry.isEmpty()) {
        isEmpty = false;
        break;
      }
    }
    if (!isEmpty) {
      System.err.println(sanitationResult);
      fail("Found errors!");
    } else {
      assertTrue(true);
    }
  }

  private Map<String, Set<String>> findBadWords(String languageCode) {
    List<Character> allowedChars = computeAllowedCharsList(languageCode);
    NoOtherCharsEvaluator testEvaluator = new NoOtherCharsEvaluator(allowedChars);
    List<Evaluator<?>> evaluators = Collections.singletonList(testEvaluator);
    Dictionary dictionary = Dictionary.getDictionary(languageCode);

    dictionary.process(evaluators);
    
    return testEvaluator.getResults();
  }
  
  private List<Character> computeAllowedCharsList(String languageCode) {
    Language lang = Language.get(languageCode);
    List<Character> allowedChars = new ArrayList<>();
    for (String entry : LanguageService.getLetters(LetterType.VOWELS, lang)) {
      if (entry.length() == 1) {
        allowedChars.add(entry.charAt(0));
      }
    }
    for (String entry : LanguageService.getLetters(LetterType.CONSONANTS, lang)) {
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
        String key = subIndex > -1 ? word.substring(subIndex, subIndex + 1) : "__";
        addEntry(key, word);
      }
    }

    @Override
    public WordForm getWordForm() {
      return WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
    }
  }
}
