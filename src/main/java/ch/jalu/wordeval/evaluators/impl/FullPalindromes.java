package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import com.google.common.collect.ImmutableList;

/**
 * Evaluator that finds proper palindromes based on the results of the
 * {@link Palindromes} evaluator, which also matches parts of a word (e.g.
 * "ette" in "better").
 */
public class FullPalindromes implements PostEvaluator<WordWithScore> {

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore<WordWithScore> resultStore) {
    ImmutableList<WordWithKey> palindromeResults =
      resultsProvider.getResultsOfEvaluatorOfType(Palindromes.class);

    for (WordWithKey entry : palindromeResults) {
      Word word = entry.getWord();
      int wordLength = word.getWithoutAccentsWordCharsOnly().length();
      if (wordLength == entry.getKey().length()) {
        resultStore.addResult(new WordWithScore(word, wordLength));
      }
    }
  }
}
