package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

/**
 * Evaluator that collects words with the most different vowels or consonants.
 */
@Getter
public class AllVowels implements PostEvaluator<WordWithKey> {

  private final LetterType letterType;

  public AllVowels(LetterType letterType) {
    this.letterType = letterType;
  }

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore<WordWithKey> resultStore) {
    ImmutableList<WordWithKey> vowelCountResults =
      resultsProvider.getResultsOfEvaluatorOfType(VowelCount.class, vw -> vw.getLetterType() == letterType);
    resultStore.addResults(vowelCountResults);
    // TODO : Check this. Does this make sense?
  }

  // TODO #50: Set export params to prefer short words with all vowels
}
