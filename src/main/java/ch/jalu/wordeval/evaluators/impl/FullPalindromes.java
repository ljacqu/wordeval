package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Evaluator that finds proper palindromes based on the results of the
 * {@link Palindromes} evaluator, which also matches parts of a word (e.g.
 * "ette" in "better").
 */
public class FullPalindromes implements PostEvaluator<WordWithScore> {

  private final List<WordWithScore> results = new ArrayList<>();

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore<WordWithScore> resultStore) {
    ImmutableList<WordWithKey> palindromeResults =
      resultsProvider.getResultsOfEvaluatorOfType(Palindromes.class);

    for (WordWithKey entry : palindromeResults) {
      Word word = entry.getWord();
      int wordLength = word.getWithoutAccentsWordCharsOnly().length();
      if (wordLength == entry.getKey().length()) {
        resultStore.addResult(new WordWithScore(word, wordLength));
        results.add(new WordWithScore(word, wordLength));
      }
    }
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
