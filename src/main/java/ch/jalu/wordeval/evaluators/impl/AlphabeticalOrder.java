package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import com.google.common.collect.ListMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Filter that saves words whose letters are alphabetical from beginning to end,
 * forwards or backwards. For example, in German "einst", each following letter
 * comes later in the alphabet.
 */
public class AlphabeticalOrder implements WordEvaluator {

  private static final int FORWARDS = -1;
  private static final int BACKWARDS = 1;

  @Getter
  private final List<WordWithScore> results = new ArrayList<>();

  @Override
  public void evaluate(Word word) {
    // TODO #15: Make locale-aware instead
    String text = word.getWithoutAccentsWordCharsOnly();
    if (areLettersOrdered(text, FORWARDS) || areLettersOrdered(text, BACKWARDS)) {
      results.add(new WordWithScore(word, text.length()));
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

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    List<WordWithScore> sortedResult = results.stream()
        .sorted(Comparator.comparing(WordWithScore::getScore).reversed())
        .toList();

    Set<Double> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = EvaluatorExportUtil.newListMultimap();
    for (WordWithScore wordWithScore : sortedResult) {
      if (uniqueValues.add(wordWithScore.getScore()) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put((int) wordWithScore.getScore(), wordWithScore.getWord().getRaw());
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }
}
