package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.result.WordWithKeyAndScore;
import com.google.common.collect.ListMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Finds segments in words that are repeated multiple times,
 * e.g. 3x "est" in af. "geestestoestand".
 */
public class RepeatedSegment implements WordEvaluator {

  @Getter
  private final List<WordWithKeyAndScore> results = new ArrayList<>();

  @Override
  public void evaluate(Word wordObject) {
    String word = wordObject.getLowercase();
    Map<String, Integer> results = new NgramGenerator(word).getResults();
    removeNgramSubsets(results);
    results.forEach((ngram, count) -> this.results.add(new WordWithKeyAndScore(wordObject, ngram, count)));
  }

  /**
   * Removes "subset" results that are covered by larger results. For example, processing the word
   * "geestestoestand" will yield the pairs (3, est), (3, es), (3, st). The last two are "contained"
   * in the first and so are removed.
   *
   * @param results the result to trim
   */
  private static void removeNgramSubsets(Map<String, Integer> results) {
    Set<String> subsets = new HashSet<>();
    for (Map.Entry<String, Integer> entry : results.entrySet()) {
      Integer count = entry.getValue();
      createNgrams(entry.getKey()).stream()
        .filter(subset -> Objects.equals(count, results.get(subset)))
        .forEach(subsets::add);
    }
    subsets.forEach(results::remove);
  }

  /**
   * Creates all possible n-grams for the given word.
   *
   * @param word the word to create n-grams for
   * @return constructed of n-grams
   */
  private static List<String> createNgrams(String word) {
    List<String> ngrams = new ArrayList<>();
    for (int start = 0; start < word.length(); ++start) {
      // need to adjust end if start == 0 or else we will also include the entire word
      int end = start == 0 ? word.length() - 1 : word.length();
      for ( ; end > start; --end) {
        ngrams.add(word.substring(start, end));
      }
    }
    return ngrams;
  }

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    // todo: Sort better, considering the key length.
    List<WordWithKeyAndScore> sortedResult = results.stream()
        .sorted(Comparator.comparing(WordWithKeyAndScore::getScore).reversed())
        .toList();

    Set<Integer> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = EvaluatorExportUtil.newListMultimap();
    for (WordWithKeyAndScore word : sortedResult) {
      if (uniqueValues.add(word.getScore()) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put(word.getScore(), word.getWord().getRaw() + " (" + word.getKey() + ")");
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }

  /**
   * Counts all n-grams of a word.
   */
  private static final class NgramGenerator {

    private final String word;
    private final int maxNgramSize;
    private final Map<String, Integer> ngramCount;

    public NgramGenerator(String word) {
      this.word = word;
      this.maxNgramSize = word.length() / 2;
      this.ngramCount = new HashMap<>();
      countNgrams();
    }

    /**
     * Returns all n-grams with multiple occurrences.
     *
     * @return collection of n-grams occurring multiple times (ngram -> count)
     */
    public Map<String, Integer> getResults() {
      return ngramCount.entrySet().stream()
        .peek(this::adjustCount)
        .filter(entry -> entry.getValue() > 1)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void countNgrams() {
      for (int i = 0; i <= word.length() - 2; ++i) {
        createNGramsAtOffset(i);
      }
    }

    private void createNGramsAtOffset(int start) {
      int end = Math.min(word.length(), start + maxNgramSize);
      while (end - start >= 2) {
        String ngram = word.substring(start, end);
        int count = nullToZero(ngramCount.get(ngram));
        ngramCount.put(ngram, ++count);
        --end;
      }
    }

    /**
     * Adjusts the count of an n-gram to ensure that it really occurs as many times as counted.
     * For instance, in "Mississippi" the initial count of "issi" is 2 but they overlap, so it
     * needs to be corrected to 1.
     *
     * @param entry the entry to adjust
     */
    private void adjustCount(Map.Entry<String, Integer> entry) {
      if (entry.getValue() > 1) {
        // int division result -> gets ceil'd automatically
        int lengthDiff = (word.length() - word.replaceAll(entry.getKey(), "").length())
            / entry.getKey().length();
        if (lengthDiff != entry.getValue()) {
          // May still not be correct...
          entry.setValue(entry.getValue() - 1);
        }
      }
    }

    private static int nullToZero(Integer i) {
      return i == null ? 0 : i;
    }
  }
}
