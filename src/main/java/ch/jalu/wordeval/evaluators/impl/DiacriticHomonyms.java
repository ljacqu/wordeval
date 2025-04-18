package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.WordGroupWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Groups words which only differ in diacritics which are not considered
 * distinct letters in the language, such as {des, dés, dès} in French or
 * {schon, schön} in German.
 */
public class DiacriticHomonyms implements AllWordsEvaluator<WordGroupWithKey> {

  private final List<WordGroupWithKey> results = new ArrayList<>();

  @Override
  public void evaluate(Collection<Word> words, ResultStore<WordGroupWithKey> resultStore) {
    SetMultimap<String, Word> wordsByNoAccentRep = words.stream()
      .collect(Multimaps.toMultimap(
        Word::getWithoutAccents,
        word -> word,
        HashMultimap::create));

    Multimaps.asMap(wordsByNoAccentRep).forEach((wordRep, wordsInGroup) -> {
      if (wordsInGroup.size() > 1) {
        resultStore.addResult(new WordGroupWithKey(wordsInGroup, wordRep));
        results.add(new WordGroupWithKey(wordsInGroup, wordRep));
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
}
