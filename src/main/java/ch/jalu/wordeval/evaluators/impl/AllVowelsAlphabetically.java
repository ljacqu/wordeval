package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ImmutableMultimap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Finds words with all vowels that appear alphabetically, such as "arbeidsonrust".
 */
public class AllVowelsAlphabetically implements PostEvaluator {

  private final List<String> vowels;

  public AllVowelsAlphabetically(Language language) {
    vowels = language.getVowels();
  }

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore resultStore) {
    ImmutableMultimap<Double, EvaluatedWord> vowelCountResults =
      resultsProvider.getResultsOfEvaluatorOfType(VowelCount.class, vc -> vc.getLetterType() == LetterType.VOWELS);

    for (Map.Entry<Double, Collection<EvaluatedWord>> entry : vowelCountResults.asMap().entrySet()) {
      for (EvaluatedWord evaluatedWord : entry.getValue()) {
        if (hasVowelsAlphabetically(evaluatedWord.getWord().getWithoutAccents())) {
          resultStore.addResult(evaluatedWord.getWord(), new EvaluationResult(evaluatedWord.getResult()));
        }
      }
    }
  }

  private boolean hasVowelsAlphabetically(String word) {
    int idx = 0;
    String curVowel = vowels.get(idx);
    for (int i = 0; i < word.length(); ++i) {
      String str = word.substring(i, i + 1);
      if (vowels.contains(str)) {
        if (str.equals(curVowel)) {
          idx++;
          curVowel = idx >= vowels.size() ? null : vowels.get(idx);
        } else {
          return false;
        }
      }
    }
    return true;
  }

  // TODO #50: Set export params to prefer short words with all vowels

}
