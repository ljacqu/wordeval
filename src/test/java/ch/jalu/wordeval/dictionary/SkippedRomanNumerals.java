package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.config.SpringContainedRunner;
import ch.jalu.wordeval.dictionary.hunspell.sanitizer.HunspellSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Prints the words of a dictionary which were skipped because they were
 * recognized as a Roman numeral.
 */
@Slf4j
public class SkippedRomanNumerals extends SpringContainedRunner {

  @Autowired
  private AppData appData;

  @Autowired
  private DataUtils dataUtils;

  public static void main(String... args) {
    runApplication(SkippedRomanNumerals.class, args);
  }

  public void run(String[] args) {
    try (Scanner sc = new Scanner(System.in)) {
      System.out.println("Enter dictionary code:");
      String code = sc.nextLine();
      findRomanNumeralSkips(code);
    }
  }

  public void findRomanNumeralSkips(String dictionaryCode) {
    Dictionary dict = appData.getDictionary(dictionaryCode);
    String fileName = dict.getFile();

    if (dict instanceof HunspellDictionary hunDict) {
      HunspellSanitizer sanitizer = hunDict.getSanitizer();
      List<String> skippedNumerals = new ArrayList<>();
      for (String line : dataUtils.readAllLines(fileName)) {
        if (sanitizer.skipLine(line)) {
          String word = extractHunspellWord(line);
          if (DictionaryUtils.isRomanNumeral(word)) {
            skippedNumerals.add(word);
          }
        }
      }
      log.info("Skipped words for '{}':\n- {}", dictionaryCode, String.join("\n- ", skippedNumerals));
    } else {
      throw new IllegalStateException("Unsupported dictionary type: " + dict.getClass());
    }
  }

  // todo: Move this - sanitizer should work on (baseWord, flags)
  private static String extractHunspellWord(String line) {
    return StringUtils.substringBefore(line, '/');
  }
}
