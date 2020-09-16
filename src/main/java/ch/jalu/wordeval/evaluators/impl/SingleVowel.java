package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;

/**
 * Evaluator collecting words which only have one distinct vowel or consonant,
 * such as "abracadabra," which only uses the vowel 'a.' 
 */
@AllArgsConstructor
public class SingleVowel implements PostEvaluator<WordWithScore> {

  private final LetterType letterType;

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
      .forEach(e -> resultStore.addResult(new WordWithScore(e.getWord(), e.getWord().getLowercase().length())));
  }
}
