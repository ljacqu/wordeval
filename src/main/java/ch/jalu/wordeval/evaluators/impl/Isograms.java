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
 * Finds isograms (words with all different letters).
 */
public class Isograms implements WordEvaluator<WordWithScore> {

  private final List<WordWithScore> results = new ArrayList<>();

  @Override
  public void evaluate(Word wordObject, ResultStore<WordWithScore> resultStore) {
    String word = wordObject.getWithoutAccentsWordCharsOnly();
    Set<Character> charList = new HashSet<>();
    for (int i = 0; i < word.length(); ++i) {
      char currentChar = word.charAt(i);
      if (!charList.add(currentChar)) {
        // Char has already been encountered, so stop
        return;
      }
    }
    resultStore.addResult(new WordWithScore(wordObject, word.length()));
    results.add(new WordWithScore(wordObject, word.length()));
  }

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
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

    return filteredResults;
  }
}
