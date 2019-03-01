package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Collects anagram groups (e.g. "acre", "care", "race").
 */
public class Anagrams implements AllWordsEvaluator {

  @Override
  public void evaluate(List<Word> words, ResultStore resultStore) {
    Multimap<String, Word> wordsBySortedChars = words.stream()
      .collect(Multimaps.toMultimap(this::sortLettersAlphabetically, Function.identity(), HashMultimap::create));

    wordsBySortedChars.asMap().forEach((sequence, groupedWords) -> {
      if (groupedWords.size() > 1) {
        String wordList = groupedWords.stream()
          .map(Word::getRaw)
          .collect(Collectors.joining(", "));
        resultStore.addResult(Iterables.get(groupedWords, 0),
          new EvaluationResult(groupedWords.size(), wordList));
      }
    });
  }

  private String sortLettersAlphabetically(Word word) {
    char[] chars = word.getWithoutAccentsWordCharsOnly().toCharArray();
    Arrays.sort(chars);
    return new String(chars);
  }
}
