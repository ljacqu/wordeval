package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.config.SpringContainedRunner;
import ch.jalu.wordeval.dictionary.DictionaryService.WordEntries;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Prints the lines of the dictionary (configurable below) as to inspect which
 * lines are removed or considered.
 */
public class DictionaryEntriesPrinter extends SpringContainedRunner {

  private static final boolean SHOW_INCLUDED_WORDS = true;
  private static final boolean SHOW_SKIPPED_WORDS = false;
  // Calculates pages of 2000 entries. Set to 0 to skip.
  private static final int INCLUDED_WORDS_PAGE = 1;

  @Autowired
  private AppData appData;

  @Autowired
  private DictionaryService dictionaryService;

  public static void main(String... args) {
    runApplication(DictionaryEntriesPrinter.class, args);
  }

  @Override
  public void run(String... args) {
    String code = "de-de";

    Dictionary dictionary = appData.getDictionary(code);

    WordEntries words = dictionaryService.processWordsForDebug(dictionary);
    System.out.println("Language: " + code);
    if (SHOW_INCLUDED_WORDS) {
      int[] skipAndLimit = calculateSkipAndLimitValues();
      words.includedLines().stream()
          .skip(skipAndLimit[0])
          .limit(skipAndLimit[1])
          .forEach(System.out::println);
    }

    if (SHOW_SKIPPED_WORDS) {
      System.out.println();
      System.out.println("Skipped lines (" + code + ")");
      words.skippedLines().forEach(System.out::println);
    }
  }

  private static int[] calculateSkipAndLimitValues() {
    if (INCLUDED_WORDS_PAGE < 1) {
      return new int[]{0, Integer.MAX_VALUE};
    }
    int pageOffset = INCLUDED_WORDS_PAGE - 1;
    return new int[]{pageOffset * 2000, 2000};
  }
}
