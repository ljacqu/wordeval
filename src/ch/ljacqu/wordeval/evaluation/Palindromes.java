package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.language.WordForm;

/**
 * Finds palindromes or palindrome-parts inside a word, e.g. "awkwa" in
 * "awkward".
 */
public class Palindromes extends Evaluator<String, String> {

  @Override
  public void processWord(String word, String rawWord) {
    for (int i = 1; i < word.length() - 1; ++i) {
      String palindrome = findPalindrome(word, i);
      if (palindrome != null) {
        addEntry(palindrome, rawWord);
      }
    }
  }
  
  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }

  /**
   * Returns the palindrome part in `word` with center `index` if there is one.
   * @param word The word to process
   * @param index The index to test at
   * @return The palindrome part, or null if none found
   */
  private String findPalindrome(String word, int index) {
    // Asymmetrical palindromes like "awkwa" 
    String palindrome = findPalindrome(word, index, 1);
    if (palindrome != null) {
      return palindrome;
    }
    // Symmetrical palindromes like "abba"
    palindrome = findPalindrome(word, index, 0);
    if (palindrome != null) {
      return palindrome;
    }
    return null;
  }

  /**
   * Checks for palindrome part in a word with `index` as middle. E.g. with word
   * as "awkward" it returns "awkwa" for index = 2 and offset = 1, null for all
   * other offsets.
   * @param word The word to process
   * @param index The index to test at
   * @param offset 0 or 1: for "abba"-like or "awkwa"-like palindromes
   * @return The palindrome part, or null if none found
   */
  private String findPalindrome(String word, int index, int offset) {
    int i = index - 1, j = index + offset;
    for (; i >= 0 && j < word.length(); --i, ++j) {
      if (word.charAt(i) != word.charAt(j)) {
        break;
      }
    }
    ++i;
    --j;

    // i < j - 1 ensures that a simple pair like "oo" in "cool" does not get
    // added: in this case, i = 1, j = 2; so i < j - 1 == false
    if (i < j - 1) {
      return word.substring(i, j + 1);
    }
    return null;
  }
}
