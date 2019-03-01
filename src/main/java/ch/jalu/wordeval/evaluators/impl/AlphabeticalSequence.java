package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;

/**
 * Filters that checks if there is a group of letters in a word that is an
 * alphabetical sequence, e.g. "rstu" in German "Erstuntersuchung."
 */
public class AlphabeticalSequence implements WordEvaluator {

  private static final int FORWARDS = -1;
  private static final int BACKWARDS = 1;

  @Override
  public void evaluate(Word word, ResultStore resultStore) {
    // TODO #15: Make locale-aware instead
    findAlphabeticalSequences(word, resultStore, FORWARDS);
    findAlphabeticalSequences(word, resultStore, BACKWARDS);
  }

  private void findAlphabeticalSequences(Word word, ResultStore resultStore, int searchDirection) {
    String text = word.getWithoutAccentsWordCharsOnly();
    int alphabeticalStreak = 1;
    String previousChar = String.valueOf(text.charAt(0));
    for (int i = 1; i <= text.length(); ++i) {
      boolean isCharInSequence = false;
      if (i < text.length()) {
        String currentChar = String.valueOf(text.charAt(i));
        isCharInSequence = previousChar.compareTo(currentChar) == searchDirection;
        if (isCharInSequence) {
          ++alphabeticalStreak;
        }
        previousChar = currentChar;
      }
      if (!isCharInSequence || i == text.length()) {
        if (alphabeticalStreak > 2) {
          String alphabeticalSequence = text.substring(i - alphabeticalStreak, i);
          resultStore.addResult(word, new EvaluationResult(alphabeticalSequence.length(), alphabeticalSequence));
        }
        alphabeticalStreak = 1;
      }
    }
  }

}
