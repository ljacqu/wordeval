package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import org.apache.commons.lang3.StringUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility task to verify how well a dictionary is being sanitized based on the characters of the words
 * that were loaded.
 */
public final class DictionarySanitationCharChecker {

  private static final AppData appData = new AppData();

  private DictionarySanitationCharChecker() {
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
      if (!TestUtil.doesDictionaryFileExist(dictionary)) {
        System.out.println("Skipping '" + dictionary.getIdentifier() + "': file does not exist");
        continue;
      }

      String allowedChars = createStringOfAllowedChars(dictionary.getLanguage());
      System.out.println("Checking '" + dictionary.getIdentifier() + "' (" + allowedChars + ")");
      Set<String> badWords = findBadWords(dictionary, allowedChars.toCharArray());
      if (!badWords.isEmpty()) {
        System.err.println(dictionary.getIdentifier() + ": " + badWords);
      } else {
        System.out.println(dictionary.getIdentifier() + " passed");
      }
    }
  }

  private static Set<String> findBadWords(Dictionary dictionary, char[] allowedChars) {
    return DictionaryProcessor.readAllWords(dictionary).stream()
      .map(Word::getWithoutAccentsWordCharsOnly)
      .filter(word -> !StringUtils.containsOnly(word, allowedChars))
      .collect(Collectors.toSet());
  }
  
  private static String createStringOfAllowedChars(Language language) {
    List<String> additions = new ArrayList<>();
    if (language.getAlphabet() == Alphabet.CYRILLIC) {
      additions.addAll(List.of("ь", "ъ"));
    } else if ("de".equals(language.getCode())) {
      additions.add("ß");
    }

    Collator collator = Collator.getInstance(language.getLocale());
    return Stream.of(additions, language.getVowels(), language.getConsonants())
      .flatMap(Collection::stream)
      .filter(letter -> letter.length() == 1)
      .distinct()
      .sorted(collator::compare)
      .collect(Collectors.joining());
  }
}
