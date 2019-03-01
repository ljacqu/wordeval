package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;

/**
 * Filter that saves words whose letters are alphabetical from beginning to end,
 * forwards or backwards. For example, in German "einst", each following letter
 * comes later in the alphabet.
 */
public class AlphabeticalOrder implements WordEvaluator {

  private static final int FORWARDS = -1;
  private static final int BACKWARDS = 1;

  @Override
  public void evaluate(Word word, ResultStore resultStore) {
    // TODO #15: Make locale-aware instead
    String text = word.getWithoutAccentsWordCharsOnly();
    if (areLettersOrdered(text, FORWARDS) || areLettersOrdered(text, BACKWARDS)) {
      resultStore.addResult(word, new EvaluationResult(text.length(), null));
    }
  }

  private static boolean areLettersOrdered(String word, int searchDirection) {
    // TODO: Replace Strings with chars
    String previousChar = String.valueOf(word.charAt(0));
    for (int i = 1; i < word.length(); ++i) {
      String currentChar = String.valueOf(word.charAt(i));
      int comparison = strcmp(previousChar, currentChar);
      if (comparison == 0 || comparison == searchDirection) {
        previousChar = currentChar;
      } else {
        // The comparison is not what we were looking for, so stop
        return false;
      }
    }
    return true;
  }

  private static int strcmp(String a, String b) {
    int comparison = a.compareToIgnoreCase(b);
    return Integer.compare(comparison, 0);
  }
}
