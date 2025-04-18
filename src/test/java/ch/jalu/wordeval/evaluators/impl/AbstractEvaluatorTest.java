package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.result.WordGroup;
import ch.jalu.wordeval.evaluators.result.WordGroupWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithKeyAndScore;
import ch.jalu.wordeval.evaluators.result.WordWithScore;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Common parent with utilities for Evaluator implementation tests.
 */
abstract class AbstractEvaluatorTest {

  protected List<Word> createWords(String... words) {
    return Arrays.stream(words).map(TestWord::new).collect(Collectors.toList());
  }

  protected static Map<String, Set<String>> groupResultsByKey(List<WordGroupWithKey> results) {
    return results.stream()
        .collect(Collectors.toMap(WordGroupWithKey::getKey, group -> unwrapWords(group.getWords())));
  }

  protected static Map<Double, Set<String>> groupByScore(List<WordWithScore> wordsWithScores) {
    return wordsWithScores.stream().collect(Collectors.groupingBy(WordWithScore::getScore))
        .entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey,
            e -> unwrapWords(e.getValue(), WordWithScore::getWord)));
  }

  protected static List<Set<String>> unwrapWordGroups(List<WordGroup> results) {
    return results.stream()
        .map(wordGroup -> unwrapWords(wordGroup.getWords()))
        .collect(Collectors.toList());
  }

  protected static Map<String, Set<String>> groupByKey(List<WordWithKey> results) {
    return results.stream()
        .collect(Collectors.groupingBy(WordWithKey::getKey))
        .entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey,
            e -> unwrapWords(e.getValue(), WordWithKey::getWord)));
  }

  protected static Map<String, Set<String>> flattenKeyAndScore(List<WordWithKeyAndScore> results) {
    return results.stream()
        .collect(Collectors.groupingBy(wwks -> wwks.getScore() + "," + wwks.getKey())).entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> unwrapWords(e.getValue(), WordWithKeyAndScore::getWord)));
  }

  private static Set<String> unwrapWords(Collection<Word> words) {
    return words.stream()
        .map(Word::getRaw)
        .collect(Collectors.toSet());
  }

  private static <T> Set<String> unwrapWords(Collection<T> items, Function<T, Word> wordGetter) {
    return unwrapWords(items.stream().map(wordGetter).collect(Collectors.toSet()));
  }
}
