package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import com.google.common.collect.ImmutableMultimap;

import java.util.Map;

/**
 * Evaluator that finds proper palindromes based on the results of the
 * {@link Palindromes} evaluator, which also matches parts of a word (e.g.
 * "ette" in "better").
 */
public class FullPalindromes implements PostEvaluator {

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore resultStore) {
    ImmutableMultimap<Double, EvaluatedWord> palindromeResults =
      resultsProvider.getResultsOfEvaluatorOfType(Palindromes.class);

    for (Map.Entry<Double, EvaluatedWord> entry : palindromeResults.entries()) {
      Word word = entry.getValue().getWord();
      int wordLength = word.getWithoutAccentsWordCharsOnly().length();
      if (wordLength == entry.getValue().getResult().getKey().length()) {
        resultStore.addResult(word, new EvaluationResult(wordLength, null));
      }
    }
  }
}
