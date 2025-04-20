package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import com.google.common.collect.ListMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Finds palindromes or palindrome-parts inside a word, e.g. "awkwa" in
 * "awkward".
 */
public class Palindromes implements WordEvaluator {

  @Getter
  private final List<WordWithKey> results = new ArrayList<>();

  @Override
  public void evaluate(Word wordObject) {
    String word = wordObject.getWithoutAccentsWordCharsOnly();
    for (int i = 1; i < word.length() - 1; ++i) {
      String palindrome = findPalindrome(word, i);
      if (palindrome != null) {
        results.add(new WordWithKey(wordObject, palindrome));
      }
    }
  }

  /**
   * Returns the palindrome part in `word` with center `index` if there is one.
   * @param word The word to process
   * @param index The index to test at
   * @return The palindrome part, or null if none found
   */
  private static String findPalindrome(String word, int index) {
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
  private static String findPalindrome(String word, int index, int offset) {
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

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    List<WordWithKey> sortedResult = results.stream()
        .sorted(Comparator.comparing((WordWithKey wwk) -> wwk.key().length()).reversed())
        .toList();

    Set<String> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = EvaluatorExportUtil.newListMultimap();
    for (WordWithKey WordWithKey : sortedResult) {
      if (uniqueValues.add(WordWithKey.key()) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put(WordWithKey.key(), WordWithKey.word().getRaw());
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }
}
