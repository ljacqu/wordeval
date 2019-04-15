package ch.jalu.wordeval.helpertask;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.runners.DictionaryProcessor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility task to verify how well a dictionary is being sanitized.
 */
public class DictionarySanitationTest {

  private static final AppData appData = new AppData();

  private DictionarySanitationTest() {
  }

  public static void main(String... args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter dictionary code to verify (or empty for all):");
    String dictCode = sc.nextLine();
    sc.close();

    Map<String, Set<String>> sanitationResult = new HashMap<>();
    Iterable<String> languageCodes = StringUtils.isEmpty(dictCode.trim())
        ? appData.getAllDictionaryCodes()
        : Collections.singletonList(dictCode);
    
    for (String languageCode : languageCodes) {
      Set<String> badWords = findBadWords(languageCode);
      if (!badWords.isEmpty()) {
        sanitationResult.put(languageCode, badWords);
      }
    }

    if (!sanitationResult.isEmpty()) {
      sanitationResult.forEach((key, value) -> System.err.println(key + ": " + value));
    } else {
      System.out.println("Verification successful");
    }
  }

  private static Set<String> findBadWords(String languageCode) {
    char[] allowedChars = createStringOfAllowedChars(languageCode);
    AppData appData = new AppData();
    Dictionary dictionary = appData.getDictionary(languageCode);

    return DictionaryProcessor.readAllWords(dictionary).stream()
      .map(Word::getWithoutAccentsWordCharsOnly)
      .filter(word -> !StringUtils.containsOnly(word, allowedChars))
      .collect(Collectors.toSet());
  }
  
  private static char[] createStringOfAllowedChars(String languageCode) {
    Language lang = appData.getLanguage(languageCode);
    List<String> additions = lang.getAlphabet() == Alphabet.CYRILLIC
      ? Arrays.asList("ь", "ъ")
      : Collections.emptyList();

    String allowedChars = Stream.of(additions, lang.getVowels(), lang.getConsonants())
      .flatMap(Collection::stream)
      .filter(letter -> letter.length() == 1)
      .distinct()
      .collect(Collectors.joining());
    return allowedChars.toCharArray();
  }
}
