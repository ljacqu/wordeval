package ch.ljacqu.wordeval.evaluation.export;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.Getter;

@Getter
public abstract class PartWordReducer {

  private NavigableMap<Number, NavigableMap<String, List<String>>> topEntries;
  private NavigableMap<Number, NavigableMap<String, Integer>> aggregatedEntries;

  public void compute(NavigableMap<String, List<String>> evaluatorResult, ExportParams params) {
    NavigableMap<Number, NavigableMap<String, List<String>>> orderedResults = order(evaluatorResult);
    topEntries = ExportObject.isolateTopEntries(orderedResults, params);

    if (topEntries.isEmpty()) {
      aggregatedEntries = aggregateEntries(orderedResults, params);
    } else {
      Number key = ExportObject.getBiggestKey(topEntries);
      aggregatedEntries = aggregateEntries(orderedResults.headMap(key, false), params);
    }
  }

  private NavigableMap<Number, NavigableMap<String, List<String>>> order(Map<String, List<String>> evaluatorResult) {
    NavigableMap<Number, NavigableMap<String, List<String>>> results = new TreeMap<>();
    for (Map.Entry<String, List<String>> entry : evaluatorResult.entrySet()) {
      addEntry(results, entry.getKey(), entry.getValue());
    }
    return results;
  }

  private NavigableMap<Number, NavigableMap<String, Integer>> aggregateEntries(
      NavigableMap<Number, NavigableMap<String, List<String>>> results, ExportParams params) {
    NavigableMap<Number, NavigableMap<String, Integer>> aggregatedEntries = new TreeMap<>();
    for (Map.Entry<Number, NavigableMap<String, List<String>>> entry : results.entrySet()) {
      aggregatedEntries.put(entry.getKey(), ExportObject.aggregateMap(entry.getValue(), params));
    }
    return aggregatedEntries;
  }

  /**
   * Computes the relevance (how "good" a result is).
   * @param key The key of the entry
   * @param words The collection of words for the key
   * @return The relevance (typically int or double); the higher the number the
   *         better the result is
   */
  protected abstract Number computeRelevance(String key, List<String> words);

  private void addEntry(SortedMap<Number, NavigableMap<String, List<String>>> results, String key, List<String> words) {
    Number relevance = computeRelevance(key, words);
    NavigableMap<String, List<String>> entry = results.get(relevance);
    if (entry == null) {
      results.put(relevance, new TreeMap<>());
      entry = results.get(relevance);
    }
    entry.put(key, words);
  }

  public static class ByLength extends PartWordReducer {
    @Override
    public Integer computeRelevance(String key, List<String> words) {
      return key.length();
    }
  }

  public static class BySize extends PartWordReducer {
    @Override
    public Integer computeRelevance(String key, List<String> words) {
      return words.size();
    }
  }

  public static class BySizeAndLength extends PartWordReducer {
    double sizePower = 1.0;
    double lengthPower = 1.0;

    public BySizeAndLength() {
    }

    public BySizeAndLength(double sizePower, double lengthPower) {
      this.sizePower = sizePower;
      this.lengthPower = lengthPower;
    }

    @Override
    public Double computeRelevance(String key, List<String> words) {
      return Math.pow(key.length(), lengthPower) + Math.pow(words.size(), sizePower);
    }
  }

}
