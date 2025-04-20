package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.ReflectionTestUtil;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.dictionary.sanitizer.Sanitizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Prints the words of a dictionary which were skipped because they were
 * recognized as a Roman numeral.
 */
@Slf4j
public final class SkippedRomanNumerals {

  private SkippedRomanNumerals() {
  }

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
    String fileName = dict.getFile();
    Sanitizer sanitizer = dict.buildSanitizer();
    Method lineToWord = ReflectionTestUtil.getMethod(Sanitizer.class, "removeDelimiters", String.class);
    
    List<String> skippedNumerals = new ArrayList<>();
    for (String line : DataUtils.readAllLines(fileName)) {
      String sanitizerResult = sanitizer.isolateWord(line);
      if (StringUtils.isEmpty(sanitizerResult)) {
        String word = (String) ReflectionTestUtil.invokeMethod(lineToWord, sanitizer, line);
        if (DictionaryUtils.isRomanNumeral(word)) {
          skippedNumerals.add(word);
        }
      }
    }
    
    log.info("Skipped words for '{}':\n- {}", dictionaryCode, String.join("\n- ", skippedNumerals));
  }
}
