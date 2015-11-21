package ch.ljacqu.wordeval.evaluation.export;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;

import ch.ljacqu.wordeval.evaluation.WordStatEvaluator;
import lombok.Getter;

/**
 * ExportObject class for evaluators of type {@link WordStatEvaluator}.
 */
@Getter
public class WordStatExport extends ExportObject {

  private final NavigableMap<Integer, List<String>> topEntries;
  private final NavigableMap<Integer, Integer> aggregatedEntries;

  /**
   * Creates a new WordStatExport object.
   * @param identifier the identifier of the export object
   * @param topEntries the collection of top entries
   * @param aggregatedEntries the collection of aggregated entries
   * @param params the export parameters
   */
  private WordStatExport(String identifier,
      NavigableMap<Integer, List<String>> topEntries,
      NavigableMap<Integer, Integer> aggregatedEntries, ExportParams params) {
    super(identifier);
    this.topEntries = ExportService.checkDescending(topEntries, params.isDescending);
    this.aggregatedEntries = ExportService.checkDescending(aggregatedEntries, params.isDescending);
  }

  /**
   * Creates a new WordStatExport object based on an evaluator's result map.
   * @param identifier The identifier of the export object
   * @param map The evaluator's result map to process
   * @return A WordStatExport instance with the transformed data
   */
  public static WordStatExport create(String identifier,
      NavigableMap<Integer, List<String>> map) {
    return create(identifier, map, ExportParams.builder().build());
  }

  /**
   * Creates a new WordStatExport object based on an evaluator's result map.
   * @param identifier The identifier of the export object
   * @param results The evaluator's result map to process
   * @param params The export parameters
   * @return A WordStatExport instance with the transformed data
   */
  public static WordStatExport create(String identifier,
      NavigableMap<Integer, List<String>> results, ExportParams params) {
    NavigableMap<Integer, List<String>> map = 
        ExportService.applyGeneralMinimum(results, toIntType(params.generalMinimum));
    NavigableMap<Integer, List<String>> topEntries = isolateTopEntries(map, params);
    topEntries = trimLargeTopEntries(topEntries, params);

    NavigableMap<Integer, Integer> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = aggregateMap(map);
    } else {
      Integer toKey = ExportService.getSmallestKey(topEntries);
      aggregatedEntries = aggregateMap(map.headMap(toKey, false));
    }
    return new WordStatExport(identifier, topEntries, aggregatedEntries, params);
  }

  /**
   * Trims a map's entries such that none has a size that exceeds the param's
   * <code>maxTopEntrySize</code> if it is not null.
   * @param topEntries The map to process
   * @param params The export parameters
   * @return Trimmed version of the map
   */
  private static <K> NavigableMap<K, List<String>> trimLargeTopEntries(
      NavigableMap<K, List<String>> topEntries, ExportParams params) {
    if (!params.maxTopEntrySize.isPresent()) {
      return topEntries;
    }
    for (Map.Entry<K, List<String>> entry : topEntries.entrySet()) {
      if (entry.getValue().size() > params.maxTopEntrySize.get()) {
        int restSize = entry.getValue().size() - params.maxTopEntrySize.get();
        // TODO #50: Find better way to shorten list if it makes sense to only
        // keep one word with the same start, for instance
        topEntries.put(entry.getKey(), reduceList(entry.getValue(), params.maxTopEntrySize.get()));
        topEntries.get(entry.getKey()).add(ExportObject.INDEX_REST + restSize);
      }
    }
    return topEntries;
  }
  
  private static Optional<Integer> toIntType(Optional<Double> value) {
    if (value.isPresent()) {
      return Optional.of(value.get().intValue());
    }
    return Optional.empty();
  }
}