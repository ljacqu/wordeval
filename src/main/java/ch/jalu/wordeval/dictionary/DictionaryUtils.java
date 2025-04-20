package ch.jalu.wordeval.dictionary;

import java.util.regex.Pattern;

/**
 * Utility methods for the dictionary.
 */
public final class DictionaryUtils {
  
  /** 
   * Regexp that matches valid Roman numerals.
   * From: https://stackoverflow.com/questions/267399/matching-valid-roman-numerals/267405#267405
   */
  private static final Pattern ROMAN_NUMERAL_PATTERN = 
      Pattern.compile("M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})", Pattern.CASE_INSENSITIVE);
  
  private DictionaryUtils() {
  }
  
  /**
   * Returns whether the given word is a Roman numeral.
   *
   * @param word the word to process
   * @return true if the word is a valid Roman numeral, false otherwise
   */
  public static boolean isRomanNumeral(String word) {
    return !word.isEmpty() && ROMAN_NUMERAL_PATTERN.matcher(word).matches();
  }

}
