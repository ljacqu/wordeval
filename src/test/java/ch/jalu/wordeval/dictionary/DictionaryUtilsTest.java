package ch.jalu.wordeval.dictionary;

import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Test for {@link DictionaryUtils}.
 */
public class DictionaryUtilsTest {
  
  @Test
  public void shouldRecognizeRomanNumerals() {
    String[] words = {"xvii", "MXIX", "XCI", "lxxxviii", "clxxxviii", "mccliv"};
    
    for (String word : words) {
      boolean result = DictionaryUtils.isRomanNumeral(word);
      if (!result) {
        fail("'" + word + "' was not recognized as Roman numeral");
      }
    }
  }
  
  @Test
  public void shouldNotRecognizeOtherWordsAsNumerals() {
    String[] words = {"house", "xoxo", "cicil", "mic", "clim", "mill", "dill", "did"};
    
    for (String word : words) {
      boolean result = DictionaryUtils.isRomanNumeral(word);
      if (result) {
        fail("'" + word + "' should not have been recognized as Roman numeral");
      }
    }
  }

}
