package ch.jalu.wordeval.dictionary;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test for {@link DictionaryService}.
 */
class DictionaryServiceTest {
  
  @Test
  void shouldRecognizeRomanNumerals() {
    String[] words = {"xvii", "MXIX", "XCI", "lxxxviii", "clxxxviii", "mccliv"};
    
    for (String word : words) {
      boolean result = DictionaryService.isRomanNumeral(word);
      if (!result) {
        fail("'" + word + "' was not recognized as Roman numeral");
      }
    }
  }
  
  @Test
  void shouldNotRecognizeOtherWordsAsNumerals() {
    String[] words = {"house", "xoxo", "cicil", "mic", "clim", "mill", "dill", "did"};
    
    for (String word : words) {
      boolean result = DictionaryService.isRomanNumeral(word);
      if (result) {
        fail("'" + word + "' should not have been recognized as Roman numeral");
      }
    }
  }

}
