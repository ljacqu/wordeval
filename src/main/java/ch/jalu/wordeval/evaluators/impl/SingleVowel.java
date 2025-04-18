package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Evaluator collecting words which only have one distinct vowel or consonant,
 * such as "abracadabra," which only uses the vowel 'a.' 
 */
@AllArgsConstructor
public class SingleVowel implements PostEvaluator<WordWithScore> {

  private final LetterType letterType;
  private final List<WordWithScore> results = new ArrayList<>();

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore<WordWithScore> resultStore) {
    ImmutableList<WordWithKey> results =
      resultsProvider.getResultsOfEvaluatorOfType(VowelCount.class, vc -> vc.getLetterType() == letterType);

    int min = results.stream()
      .mapToInt(e -> e.getKey().length())
      .filter(len -> len > 0)
      .min()
      .orElseThrow(() -> new IllegalStateException("Could not get minimum - no words with letter type?"));

    results.stream()
      .filter(e -> e.getKey().length() == min)
      .forEach(e -> {
        resultStore.addResult(new WordWithScore(e.getWord(), e.getWord().getLowercase().length()));
        this.results.add(new WordWithScore(e.getWord(), e.getWord().getLowercase().length()));
      });
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

  @Override
  public String getId() {
    return switch (letterType) {
      case VOWELS -> "SingleVowel";
      case CONSONANTS -> "SingleConsonant";
    };
  }
}
