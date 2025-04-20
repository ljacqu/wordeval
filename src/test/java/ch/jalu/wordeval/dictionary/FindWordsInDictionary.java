package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.config.SpringContainedRunner;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility task to verify if a certain word appears in a given dictionary.
 * (Useful to make sure a custom sanitizer is not too strict.)
 */
public class FindWordsInDictionary extends SpringContainedRunner {

  @Autowired
  private AppData appData;

  public static void main(String... args) {
    runApplication(FindWordsInDictionary.class, args);
  }

  public void run(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      System.out.println("Enter language code of dictionary:");
      String code = scanner.nextLine();
      Dictionary dictionary = appData.getDictionary(code);

      System.out.println("Enter words to find (comma-separated)");
      String line = scanner.nextLine();
      scanner.close();

      Set<String> wordsToFind = Arrays.stream(line.split(","))
          .map(String::trim).collect(Collectors.toSet());
      findWordsInDict(dictionary, wordsToFind);
    }
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
