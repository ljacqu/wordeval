package ch.ljacqu.wordeval.evaluation.export;

import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * ExportObject class for evaluators of type WordStatEvaluator.
 */
public class WordStatExport extends ExportObject {

  private static final long serialVersionUID = 1L;

  private final SortedMap<Integer, List<String>> topEntries;
  private final SortedMap<Integer, Integer> aggregatedEntries;

  public WordStatExport(String identifier,
      SortedMap<Integer, List<String>> topEntries,
      SortedMap<Integer, Integer> aggregatedEntries) {
    super(identifier);
    this.topEntries = topEntries;
    this.aggregatedEntries = aggregatedEntries;
  }

  public SortedMap<Integer, List<String>> getTopEntries() {
    return Collections.unmodifiableSortedMap(topEntries);
  }

  public SortedMap<Integer, Integer> getAggregatedEntries() {
    return Collections.unmodifiableSortedMap(aggregatedEntries);
  }

  public static WordStatExport create(String identifier,
      NavigableMap<Integer, List<String>> map) {
    return create(identifier, map, new ExportParamsBuilder().build());
  }

  public static WordStatExport create(String identifier,
      NavigableMap<Integer, List<String>> map, ExportParams params) {
    SortedMap<Integer, List<String>> topEntries = getBiggestKeys(map, params);

    SortedMap<Integer, Integer> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = new TreeMap<>();
    } else {
      Integer toKey = topEntries.firstKey();
      aggregatedEntries = computeAggregatedMap(map, toKey, params);
    }
    return new WordStatExport(identifier, topEntries, aggregatedEntries);
  }
}
