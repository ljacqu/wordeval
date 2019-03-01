package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ImmutableMultimap;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * Evaluator collecting words which only have one distinct vowel or consonant,
 * such as "abracadabra," which only uses the vowel 'a.' 
 */
@AllArgsConstructor
public class SingleVowel implements PostEvaluator {

  private final LetterType letterType;

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore resultStore) {
    ImmutableMultimap<Double, EvaluatedWord> results =
      resultsProvider.getResultsOfEvaluatorOfType(VowelCount.class, vc -> vc.getLetterType() == letterType);

    int min = results.entries().stream()
      .mapToInt(e -> e.getKey().intValue())
      .min()
      .orElseThrow(() -> new IllegalStateException("Could not get minimum - no words with letter type?"));

    if (min == 0) {
      min = 1;
    }

    // TODO: This just needs a proper reducer / params and we can just take the results of VowelCount
    for (Map.Entry<Double, EvaluatedWord> entry : results.entries()) {
      if (entry.getKey().intValue() <= min) {
        resultStore.addResult(entry.getValue().getWord(), new EvaluationResult(entry.getKey(), null));
      }
    }
  }
}
