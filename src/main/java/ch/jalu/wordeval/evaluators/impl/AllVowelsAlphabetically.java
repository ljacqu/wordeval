package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.processing.AllWordsEvaluatorProvider;
import ch.jalu.wordeval.evaluators.result.WordGroupWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ListMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Finds words with all vowels that appear alphabetically, such as "arbeidsonrust".
 */
public class AllVowelsAlphabetically implements PostEvaluator {

  private final List<String> vowels;
  @Getter
  private final List<WordGroupWithKey> results = new ArrayList<>();

  public AllVowelsAlphabetically(Language language) {
    vowels = language.getVowels();
  }

  @Override
  public void evaluate(AllWordsEvaluatorProvider allWordsEvaluatorProvider) {
    VowelCount vowelCount =
        allWordsEvaluatorProvider.getEvaluator(VowelCount.class, vc -> vc.getLetterType() == LetterType.VOWELS);

    List<WordGroupWithKey> wordGroupsByKey = vowelCount.getResults().stream()
        .filter(entry -> !entry.getKey().isEmpty())
        .filter(entry -> hasVowelsAlphabetically(entry.getWord().getWithoutAccents()))
        .collect(Collectors.groupingBy(WordWithKey::getKey,
            Collectors.mapping(WordWithKey::getWord, Collectors.toSet())))
        .entrySet().stream()
        .map(e -> new WordGroupWithKey(e.getValue(), e.getKey()))
        .toList();

    results.addAll(wordGroupsByKey);
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

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    Comparator<WordGroupWithKey> comparator = Comparator.comparingInt((WordGroupWithKey group) -> group.getKey().length())
        .reversed(); // todo: unit test

    List<WordGroupWithKey> sortedResult = results.stream()
        .sorted(comparator)
        .toList();

    Set<Integer> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = EvaluatorExportUtil.newListMultimap();
    for (WordGroupWithKey wordGroup : sortedResult) {
      int score = wordGroup.getKey().length();
      if (uniqueValues.add(score) && uniqueValues.size() > topScores) {
        break;
      }
      List<String> wordList = wordGroup.getWords().stream()
          .map(Word::getRaw)
          .toList();
      filteredResults.put(wordGroup.getKey(), wordList);
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }
}
