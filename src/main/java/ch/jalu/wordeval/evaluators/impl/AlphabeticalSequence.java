package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Filters that checks if there is a group of letters in a word that is an
 * alphabetical sequence, e.g. "rstu" in German "Erstuntersuchung."
 */
public class AlphabeticalSequence implements WordEvaluator<WordWithKey> {

  private static final int FORWARDS = -1;
  private static final int BACKWARDS = 1;

  @Getter
  private final List<WordWithKey> results = new ArrayList<>();

  @Override
  public void evaluate(Word word) {
    // TODO #15: Make locale-aware instead
    findAlphabeticalSequences(word, FORWARDS);
    findAlphabeticalSequences(word, BACKWARDS);
  }

  private void findAlphabeticalSequences(Word word, int searchDirection) {
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
          results.add(new WordWithKey(word, alphabeticalSequence));
        }
        alphabeticalStreak = 1;
      }
    }
  }

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    List<WordWithKey> sortedResult = results.stream()
        .sorted(Comparator.<WordWithKey>comparingInt(wordWithKey -> wordWithKey.getKey().length()).reversed())
        .toList();

    Set<Integer> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = ArrayListMultimap.create();
    for (WordWithKey wordWithKey : sortedResult) {
      int score = wordWithKey.getKey().length();
      if (uniqueValues.add(score) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put(score, wordWithKey.getWord().getRaw() + " (" + wordWithKey.getKey() + ")");
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }
}
