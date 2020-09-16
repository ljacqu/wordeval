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
import java.util.List;
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

    Iterable<Dictionary> dictionaries = StringUtils.isBlank(dictCode)
        ? appData.getAllDictionaries()
        : Collections.singletonList(appData.getDictionary(dictCode.trim()));
    
    for (Dictionary dictionary : dictionaries) {
      Set<String> badWords = findBadWords(dictionary);
      if (!badWords.isEmpty()) {
        System.err.println(dictionary.getIdentifier() + ": " + badWords);
      } else {
        System.out.println(dictionary.getIdentifier() + " passed");
      }
    }
  }

  private static Set<String> findBadWords(Dictionary dictionary) {
    char[] allowedChars = createStringOfAllowedChars(dictionary.getLanguage());
    return DictionaryProcessor.readAllWords(dictionary).stream()
      .map(Word::getWithoutAccentsWordCharsOnly)
      .filter(word -> !StringUtils.containsOnly(word, allowedChars))
      .collect(Collectors.toSet());
  }
  
  private static char[] createStringOfAllowedChars(Language language) {
    List<String> additions = language.getAlphabet() == Alphabet.CYRILLIC
      ? Arrays.asList("ь", "ъ")
      : Collections.emptyList();

    String allowedChars = Stream.of(additions, language.getVowels(), language.getConsonants())
      .flatMap(Collection::stream)
      .filter(letter -> letter.length() == 1)
      .distinct()
      .collect(Collectors.joining());
    return allowedChars.toCharArray();
  }
}
