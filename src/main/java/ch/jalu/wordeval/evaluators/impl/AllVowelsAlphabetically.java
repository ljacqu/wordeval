package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.evaluators.result.WordGroupWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Finds words with all vowels that appear alphabetically, such as "arbeidsonrust".
 */
public class AllVowelsAlphabetically implements PostEvaluator<WordGroupWithKey> {

  private final List<String> vowels;

  public AllVowelsAlphabetically(Language language) {
    vowels = language.getVowels();
  }

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore<WordGroupWithKey> resultStore) {
    ImmutableList<WordWithKey> vowelCountResults =
      resultsProvider.getResultsOfEvaluatorOfType(VowelCount.class, vc -> vc.getLetterType() == LetterType.VOWELS);

    List<WordGroupWithKey> wordGroupsByKey = vowelCountResults.stream()
      .filter(entry -> hasVowelsAlphabetically(entry.getWord().getWithoutAccents()))
      .collect(Collectors.groupingBy(WordWithKey::getKey,
        Collectors.mapping(WordWithKey::getWord, Collectors.toSet())))
      .entrySet().stream()
      .map(e -> new WordGroupWithKey(e.getValue(), e.getKey()))
      .collect(Collectors.toList());

    resultStore.addResults(wordGroupsByKey);
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
