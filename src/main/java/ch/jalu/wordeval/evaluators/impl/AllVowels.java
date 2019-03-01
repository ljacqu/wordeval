package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ImmutableMultimap;

/**
 * Evaluator that collects words with the most different vowels or consonants.
 */
public class AllVowels implements PostEvaluator {

  private final LetterType letterType;

  public AllVowels(LetterType letterType) {
    this.letterType = letterType;
  }

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore resultStore) {
    ImmutableMultimap<Double, EvaluatedWord> vowelCountResults =
      resultsProvider.getResultsOfEvaluatorOfType(VowelCount.class, vw -> vw.getLetterType() == letterType);
    resultStore.addResults(vowelCountResults.values());
    // TODO : Check this. Does this make sense?
  }

  // TODO #50: Set export params to prefer short words with all vowels
}
