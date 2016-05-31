package ch.jalu.wordeval.helpertask;

import ch.jalu.wordeval.AppData;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.DictionarySettings;
import ch.jalu.wordeval.dictionary.WordForm;
import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.evaluation.PartWordEvaluator;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LanguageService;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Utility task to verify how well a dictionary is being sanitized.
 */
public class DictionarySanitationTest {
  
  static {
    AppData.init();
  }

  private DictionarySanitationTest() { }
  

  public static void main(String... args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter dictionary code to verify (or empty for all):");
    String dictCode = sc.nextLine();
    sc.close();

    Map<String, Multimap<String, String>> sanitationResult = new HashMap<>();
    Iterable<String> languageCodes = StringUtils.isEmpty(dictCode.trim())
        ? DictionarySettings.getAllCodes()
        : Collections.singletonList(dictCode);
    
    for (String languageCode : languageCodes) {
      Multimap<String, String> badWords = findBadWords(languageCode);
      if (!badWords.isEmpty()) {
        sanitationResult.put(languageCode, badWords);
      }
    }

    if (!sanitationResult.isEmpty()) {
      sanitationResult.entrySet().stream()
          .forEach(e -> System.err.println(e.getKey() + ": " + e.getValue()));
    } else {
      System.out.println("Verification successful");
    }
  }

  private static Multimap<String, String> findBadWords(String languageCode) {
    List<Character> allowedChars = computeAllowedCharsList(languageCode);
    NoOtherCharsEvaluator testEvaluator = new NoOtherCharsEvaluator(allowedChars);
    List<Evaluator<?>> evaluators = Collections.singletonList(testEvaluator);
    Dictionary dictionary = Dictionary.getDictionary(languageCode);

    dictionary.process(evaluators);
    
    return testEvaluator.getResults();
  }
  
  private static List<Character> computeAllowedCharsList(String languageCode) {
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
    if (Alphabet.CYRILLIC.equals(lang.getAlphabet())) {
      allowedChars.add('ь');
      allowedChars.add('ъ');
    }
    return allowedChars;
  }

  private static final class NoOtherCharsEvaluator extends PartWordEvaluator {
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
