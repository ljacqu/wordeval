package ch.jalu.wordeval.extra;

import ch.jalu.wordeval.AppData;
import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.ReflectionTestUtil;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.DictionaryService;
import ch.jalu.wordeval.dictionary.Sanitizer;
import lombok.extern.log4j.Log4j2;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Prints the words of a dictionary which were skipped because they were
 * recognized as a Roman numeral.
 */
@Ignore
@Log4j2
public class UtilSkippedRomanNumerals {
  
  private static final String DICTIONARY = "en-us";
  
  static {
    AppData.init();
  }
  
  @Test
  public void findRomanNumeralSkips() {
    Dictionary dict = Dictionary.getDictionary(DICTIONARY);
    String fileName = (String) ReflectionTestUtil.getField(Dictionary.class, dict, "fileName");
    Sanitizer sanitizer = (Sanitizer) ReflectionTestUtil.getField(Dictionary.class, dict, "sanitizer");
    if (sanitizer.getClass() != Sanitizer.class) {
      log.info("Custom sanitizer of type '{}' detected for language '{}'", 
          sanitizer.getClass().getSimpleName(), DICTIONARY);
    }
    Method lineToWord = ReflectionTestUtil.getMethod(Sanitizer.class, "removeDelimiters", String.class);
    
    DataUtils dataUtils = new DataUtils();
    List<String> skippedNumerals = new ArrayList<>();
    for (String line : dataUtils.readFileLines(fileName)) {
      String sanitizerResult = sanitizer.isolateWord(line);
      if (sanitizerResult.isEmpty()) {
        String word = (String) ReflectionTestUtil.invokeMethod(lineToWord, sanitizer, line);
        if (DictionaryService.isRomanNumeral(word)) {
          skippedNumerals.add(word);
        }
      }
    }
    
    log.info("Skipped words for '{}':\n- {}", DICTIONARY, String.join("\n- ", skippedNumerals));
  }

}
