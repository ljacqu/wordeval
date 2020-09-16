package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.WordGroupWithKey;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * Collects anagram groups (e.g. "acre", "care", "race").
 */
public class Anagrams implements AllWordsEvaluator<WordGroupWithKey> {

  @Override
  public void evaluate(Collection<Word> words, ResultStore<WordGroupWithKey> resultStore) {
    Multimap<String, Word> wordsBySortedChars = words.stream()
      .collect(Multimaps.toMultimap(this::sortLettersAlphabetically, Function.identity(), HashMultimap::create));

    wordsBySortedChars.asMap().forEach((sequence, groupedWords) -> {
      if (groupedWords.size() > 1) {
        resultStore.addResult(new WordGroupWithKey((Set) groupedWords, sequence));
      }
    });
  }

  private String sortLettersAlphabetically(Word word) {
    char[] chars = word.getWithoutAccentsWordCharsOnly().toCharArray();
    Arrays.sort(chars);
    return new String(chars);
  }
}
