package ch.ljacqu.wordeval.extra;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ch.ljacqu.wordeval.AppData;
import ch.ljacqu.wordeval.DataUtils;
import ch.ljacqu.wordeval.TestUtil;
import ch.ljacqu.wordeval.dictionary.Dictionary;
import ch.ljacqu.wordeval.dictionary.DictionaryService;
import ch.ljacqu.wordeval.dictionary.Sanitizer;
import lombok.extern.log4j.Log4j2;

/**
 * Prints the words of a dictionary which were skipped because they were
 * recognized as a Roman numeral.
 */
@Log4j2
public class UtilSkippedRomanNumerals {
  
  private static final String DICTIONARY = "en-us";
  
  static {
    AppData.init();
  }
  
  @Test
  @Ignore
  public void findRomanNumeralSkips() {
    Dictionary dict = Dictionary.getDictionary(DICTIONARY);
    String fileName = (String) TestUtil.R.getField(Dictionary.class, dict, "fileName");
    Sanitizer sanitizer = (Sanitizer) TestUtil.R.getField(Dictionary.class, dict, "sanitizer");
    if (sanitizer.getClass() != Sanitizer.class) {
      log.info("Custom sanitizer of type '{}' detected for language '{}'", 
          sanitizer.getClass().getSimpleName(), DICTIONARY);
    }
    Method lineToWord = TestUtil.R.getMethod(Sanitizer.class, "removeDelimiters", String.class);
    
    DataUtils dataUtils = new DataUtils();
    List<String> skippedNumerals = new ArrayList<>();
    for (String line : dataUtils.readFileLines(fileName)) {
      String[] wordForms = sanitizer.computeForms(line);
      if (wordForms.length == 0) {
        String word = (String) TestUtil.R.invokeMethod(lineToWord, sanitizer, line);
        if (DictionaryService.isRomanNumeral(word)) {
          skippedNumerals.add(word);
        }
      }
    }
    
    log.info("Skipped words for '{}':\n- {}", DICTIONARY, String.join("\n- ", skippedNumerals));
  }
}
