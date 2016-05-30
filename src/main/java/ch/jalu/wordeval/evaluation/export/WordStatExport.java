package ch.jalu.wordeval.evaluation.export;

import ch.jalu.wordeval.evaluation.WordStatEvaluator;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;

/**
 * ExportObject class for evaluators of type {@link WordStatEvaluator}.
 */
@Getter
public class WordStatExport extends ExportObject {

  /**
   * Creates a new WordStatExport object.
   *
   * @param identifier the identifier of the export object
   * @param topEntries the collection of top entries
   * @param aggregatedEntries the collection of aggregated entries
   * @param isDescending whether or not the maps should be transformed to descending
   */
  private WordStatExport(String identifier,
      NavigableMap<Integer, List<String>> topEntries,
      NavigableMap<Integer, Integer> aggregatedEntries, boolean isDescending) {
    super(identifier,
        ExportObjectService.checkDescending(topEntries, isDescending),
        ExportObjectService.checkDescending(aggregatedEntries, isDescending));
  }

  @Override
  public NavigableMap<Integer, List<String>> getTopEntries() {
    return (NavigableMap<Integer, List<String>>) super.getTopEntries();
  }

  @Override
  public NavigableMap<Integer, Integer> getAggregatedEntries() {
    return (NavigableMap<Integer, Integer>) super.getAggregatedEntries();
  }

  /**
   * Creates a new WordStatExport object based on an evaluator's result map.
   *
   * @param identifier the identifier of the export object
   * @param results the evaluator's result map to process
   * @param params the export parameters, or null for default parameters
   * @return a WordStatExport instance with the transformed data
   */
  public static WordStatExport create(String identifier, NavigableMap<Integer, List<String>> results,
                                      ExportParams params) {
    ExportParams exportParams = params == null ? ExportParams.defaultValues() : params;
    NavigableMap<Integer, List<String>> map =
        ExportObjectService.applyGeneralMinimum(results, toIntType(exportParams.generalMinimum));
    NavigableMap<Integer, List<String>> topEntries = ExportObjectService.isolateTopEntries(map, exportParams);
    topEntries = trimLargeTopEntries(topEntries, exportParams);

    NavigableMap<Integer, Integer> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = ExportObjectService.aggregateMap(map);
    } else {
      Integer toKey = ExportService.getSmallestKey(topEntries);
      aggregatedEntries = ExportObjectService.aggregateMap(map.headMap(toKey, false));
    }
    return new WordStatExport(identifier, topEntries, aggregatedEntries, exportParams.isDescending);
  }

  /**
   * Trims a map's entries such that none has a size that exceeds the param's
   * <code>maxTopEntrySize</code> if it is not null.
   *
   * @param topEntries the map to process
   * @param params the export parameters
   * @return trimmed version of the map
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
        topEntries.put(entry.getKey(), ExportObjectService.reduceList(entry.getValue(), params.maxTopEntrySize.get()));
        topEntries.get(entry.getKey()).add(INDEX_REST + restSize);
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