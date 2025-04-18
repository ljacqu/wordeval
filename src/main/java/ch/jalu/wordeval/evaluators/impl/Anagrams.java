package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.WordGroupWithKey;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Collects anagram groups (e.g. "acre", "care", "race").
 */
public class Anagrams implements AllWordsEvaluator<WordGroupWithKey> {

  private final List<WordGroupWithKey> results = new ArrayList<>();

  @Override
  public void evaluate(Collection<Word> words, ResultStore<WordGroupWithKey> resultStore) {
    SetMultimap<String, Word> wordsBySortedChars = words.stream()
      .collect(Multimaps.toMultimap(this::sortLettersAlphabetically, Function.identity(), HashMultimap::create));

    Multimaps.asMap(wordsBySortedChars).forEach((sequence, groupedWords) -> {
      if (groupedWords.size() > 1) {
        resultStore.addResult(new WordGroupWithKey(groupedWords, sequence));
        results.add(new WordGroupWithKey(groupedWords, sequence));
      }
    });
  }

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    Comparator<WordGroupWithKey> comparator = Comparator.comparingInt((WordGroupWithKey group) -> group.getWords().size())
        .thenComparing(group -> group.getKey().length())
        .reversed(); // todo: unit test

    List<WordGroupWithKey> sortedResult = results.stream()
        .sorted(comparator)
        .toList();

    Set<Integer> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = ArrayListMultimap.create();
    for (WordGroupWithKey wordGroup : sortedResult) {
      int score = wordGroup.getWords().size();
      if (uniqueValues.add(score) && uniqueValues.size() > topScores) {
        break;
      }
      List<String> wordList = wordGroup.getWords().stream()
          .map(Word::getRaw)
          .toList();
      filteredResults.put(score, wordList);
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }

  private String sortLettersAlphabetically(Word word) {
    char[] chars = word.getWithoutAccentsWordCharsOnly().toCharArray();
    Arrays.sort(chars);
    return new String(chars);
  }
}
