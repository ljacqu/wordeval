package ch.ljacqu.wordeval.evaluation.export;

import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Aggregated version of an evaluator's results to export.
 * @param <K> The class the evaluator uses as key
 */
public final class NumericExportResult extends ExportResult {

  private final String identifier;
  private final SortedMap<Integer, List<String>> topEntries;
  private final SortedMap<Integer, Integer> aggregatedEntries;

  public NumericExportResult(String identifier,
      SortedMap<Integer, List<String>> topEntries,
      SortedMap<Integer, Integer> aggregatedEntries) {
    this.identifier = identifier;
    this.topEntries = topEntries;
    this.aggregatedEntries = aggregatedEntries;
  }

  public String getIdentifier() {
    return identifier;
  }

  public SortedMap<Integer, List<String>> getTopEntries() {
    return Collections.unmodifiableSortedMap(topEntries);
  }

  public SortedMap<Integer, Integer> getAggregatedEntries() {
    return Collections.unmodifiableSortedMap(aggregatedEntries);
  }

  @Override
  public String toString() {
    return "ExportResult [identifier=" + identifier + ", topEntries="
        + topEntries + ", aggregatedEntries=" + aggregatedEntries + "]";
  }

  public static NumericExportResult createInstance(String identifier,
      int topKeys, NavigableMap<Integer, List<String>> map) {
    NavigableMap<Integer, List<String>> topEntries = getBiggestKeys(map,
        topKeys);

    NavigableMap<Integer, Integer> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = new TreeMap<>();
    } else {
      Integer toKey = topEntries.firstKey();
      aggregatedEntries = computeAggregatedMap(map, toKey);
    }
    return new NumericExportResult(identifier, topEntries, aggregatedEntries);
  }
}
