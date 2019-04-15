package ch.jalu.wordeval.helpertask;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.runners.DictionaryProcessor;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility task to verify if a certain word appears in a given dictionary.
 * (Useful to make sure a custom sanitizer is not too strict.)
 */
public class FindWordsInDictionary {

  private FindWordsInDictionary() { }

  public static void main(String... args) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter language code of dictionary:");
    String code = scanner.nextLine();
    AppData appData = new AppData();
    Dictionary dictionary = appData.getDictionary(code);

    System.out.println("Enter words to find (comma-separated)");
    String line = scanner.nextLine();
    scanner.close();

    Set<String> wordsToFind = Arrays.stream(line.split(","))
        .map(String::trim).collect(Collectors.toSet());
    findWordsInDict(dictionary, wordsToFind);
  }

  private static void findWordsInDict(Dictionary dictionary, Set<String> wordsToFind) {
    Set<String> actualWords = DictionaryProcessor.readAllWords(dictionary).stream()
      .map(Word::getLowercase)
      .collect(Collectors.toSet());

    Set<String> missingWords = Sets.difference(wordsToFind, actualWords);
    if (missingWords.isEmpty()) {
      System.out.println("Success -- found all words");
    } else {
      System.out.println("Words missing: " + missingWords);
    }
  }
}
