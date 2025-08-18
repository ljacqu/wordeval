package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.config.SpringContainedRunner;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.HunspellLineProcessor;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.RootAndAffixes;
import lombok.extern.slf4j.Slf4j;
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
      HunspellLineProcessor lineProcessor = hunDict.getLineProcessor();
      List<String> skippedNumerals = new ArrayList<>();
      for (String line : dataUtils.readAllLines(fileName)) {
        if (lineProcessor.split(line).isEmpty()) {
          RootAndAffixes rootAndAffixes = lineProcessor.splitWithoutValidation(line);
          if (DictionaryUtils.isRomanNumeral(rootAndAffixes.root())) {
            skippedNumerals.add(rootAndAffixes.root());
          }
        }
      }
      log.info("Skipped words for '{}':\n- {}", dictionaryCode, String.join("\n- ", skippedNumerals));
    } else {
      throw new IllegalStateException("Unsupported dictionary type: " + dict.getClass());
    }
  }
}
