package ch.jalu.wordeval.helpertask;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.ReflectionTestUtil;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.dictionary.DictionaryService;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.Sanitizer;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Prints the words of a dictionary which were skipped because they were
 * recognized as a Roman numeral.
 */
@Log4j2
public class UtilSkippedRomanNumerals {

  public static void main(String... args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter dictionary code:");
    String code = sc.nextLine();
    sc.close();
    findRomanNumeralSkips(code);
  }

  public static void findRomanNumeralSkips(String dictionaryCode) {
    AppData appData = new AppData();
    Dictionary dict = appData.getDictionary(dictionaryCode);
    String fileName = (String) ReflectionTestUtil.getField(Dictionary.class, dict, "file");
    Sanitizer sanitizer = (Sanitizer) ReflectionTestUtil.getField(Dictionary.class, dict, "sanitizer");
    if (sanitizer.getClass() != Sanitizer.class) {
      log.info("Custom sanitizer of type '{}' detected for language '{}'", 
          sanitizer.getClass().getSimpleName(), dictionaryCode);
    }
    Method lineToWord = ReflectionTestUtil.getMethod(Sanitizer.class, "removeDelimiters", String.class);
    
    List<String> skippedNumerals = new ArrayList<>();
    for (String line : DataUtils.readAllLines(fileName)) {
      String sanitizerResult = sanitizer.isolateWord(line);
      if (sanitizerResult.isEmpty()) {
        String word = (String) ReflectionTestUtil.invokeMethod(lineToWord, sanitizer, line);
        if (DictionaryService.isRomanNumeral(word)) {
          skippedNumerals.add(word);
        }
      }
    }
    
    log.info("Skipped words for '{}':\n- {}", dictionaryCode, String.join("\n- ", skippedNumerals));
  }

}
