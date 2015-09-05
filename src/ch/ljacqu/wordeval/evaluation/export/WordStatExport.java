package ch.ljacqu.wordeval.evaluation.export;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;

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
    return topEntries;
  }

  public SortedMap<Integer, Integer> getAggregatedEntries() {
    return aggregatedEntries;
  }

  /**
   * Creates a new WordStatExport object based on an evaluator's result map.
   * @param identifier The identifier of the export object
   * @param map The evaluator's result map to process
   * @return A WordStatExport instance with the transformed data
   */
  public static WordStatExport create(String identifier,
      NavigableMap<Integer, List<String>> map) {
    return create(identifier, map, new ExportParamsBuilder().build());
  }

  /**
   * Creates a new WordStatExport object based on an evaluator's result map.
   * @param identifier The identifier of the export object
   * @param map The evaluator's result map to process
   * @param params The export parameters
   * @return A WordStatExport instance with the transformed data
   */
  public static WordStatExport create(String identifier,
      NavigableMap<Integer, List<String>> map, ExportParams params) {
    SortedMap<Integer, List<String>> topEntries = getBiggestKeys(map, params);
    topEntries = trimLargeTopEntries(topEntries, params);

    SortedMap<Integer, Integer> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = aggregateMap(map, params);
    } else {
      Integer toKey = params.isDescending ? topEntries.lastKey() : topEntries
          .firstKey();
      aggregatedEntries = aggregateMap(map.headMap(toKey, false), params);
    }
    return new WordStatExport(identifier, topEntries, aggregatedEntries);
  }

  /**
   * Trims a map's entries such that none has a size that exceeds the param's
   * <code>maxTopEntrySize</code> if it is not null.
   * @param topEntries The map to process
   * @param params The export parameters
   * @return Trimmed version of the map
   */
  private static <K> SortedMap<K, List<String>> trimLargeTopEntries(
      SortedMap<K, List<String>> topEntries, ExportParams params) {
    if (params.maxTopEntrySize == null) {
      return topEntries;
    }
    for (Map.Entry<K, List<String>> entry : topEntries.entrySet()) {
      if (entry.getValue().size() > params.maxTopEntrySize) {
        int restSize = entry.getValue().size() - params.maxTopEntrySize;
        // TODO Find better way to shorten list if it makes sense to only keep
        // one word with the same start, for instance
        topEntries.put(entry.getKey(),
            entry.getValue().subList(0, params.maxTopEntrySize));
        topEntries.get(entry.getKey()).add(ExportObject.INDEX_REST + restSize);
      }
    }
    return topEntries;
  }
}
