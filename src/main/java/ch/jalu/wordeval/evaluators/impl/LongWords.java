package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Filters the words by length, with the intention to get the longest words of
 * the dictionary.
 */
public class LongWords implements WordEvaluator<WordWithScore> {

  /** Ignore any words whose length is less than the minimum length. */
  private static final int MIN_LENGTH = 6;

  private final List<WordWithScore> results = new ArrayList<>();

  @Override
  public void evaluate(Word word, ResultStore<WordWithScore> resultStore) {
    int length = word.getLowercase().length();
    if (length >= MIN_LENGTH) {
      resultStore.addResult(new WordWithScore(word, length));
      results.add(new WordWithScore(word, length));
    }
  }

  @Override
  public List<ListMultimap<Object, Object>> getTopResults(int topScores, int maxLimit) {
    List<WordWithScore> sortedResult = results.stream()
        .sorted(Comparator.comparing(WordWithScore::getScore).reversed())
        .toList();

    Set<Double> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = ArrayListMultimap.create();
    for (WordWithScore wordWithScore : sortedResult) {
      if (uniqueValues.add(wordWithScore.getScore()) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put((int) wordWithScore.getScore(), wordWithScore.getWord().getRaw());
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return List.of(filteredResults);
  }
}
