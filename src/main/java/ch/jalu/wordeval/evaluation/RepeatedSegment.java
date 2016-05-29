package ch.jalu.wordeval.evaluation;

import java.util.ArrayList;
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
public class RepeatedSegment extends PartWordEvaluator {

  @Override
  public void processWord(String word, String rawWord) {
    Map<String, Integer> results = new NgramCollector(word).getResults();
    removeNgramSubsets(results);
    results.forEach((ngram, count) -> addEntry(count + "," + ngram, word));
  }

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

  private static List<String> createNgrams(String word) {
    List<String> ngrams = new ArrayList<>();
    for (int start = 0; start < word.length(); ++start) {
      int end = start == 0 ? word.length() - 1 : word.length();
      for ( ; end > start; --end) {
        ngrams.add(word.substring(start, end));
      }
    }
    return ngrams;
  }

  private static final class NgramCollector {

    private final String word;
    private final int maxNgramSize;
    private final Map<String, Integer> ngramCount;

    private NgramCollector(String word) {
      this.word = word;
      this.maxNgramSize = word.length() / 2;
      this.ngramCount = new HashMap<>();
      countNgrams();
    }

    public Map<String, Integer> getResults() {
      return ngramCount.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
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
        Integer count = nullToZero(ngramCount.get(ngram));
        ngramCount.put(ngram, ++count);
        --end;
      }
    }

    private void adjustCount(Map.Entry<String, Integer> entry) {
      // int division result -> gets ceil'd automatically
      int lengthDiff = (word.length() - word.replaceAll(entry.getKey(), "").length())
          / entry.getKey().length();
      if (lengthDiff != entry.getValue()) {
        // May still not be correct...
        entry.setValue(entry.getValue() - 1);
      }
    }

    private static int nullToZero(Integer i) {
      return i == null ? 0 : i;
    }
  }
}
