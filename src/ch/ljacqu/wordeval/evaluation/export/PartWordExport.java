package ch.ljacqu.wordeval.evaluation.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import lombok.Getter;

/**
 * ExportObject class for evaluators of type PartWordEvaluator.
 */
@Getter
public final class PartWordExport extends ExportObject {

  private final NavigableMap<Double, NavigableMap<String, Object>> topEntries;
  private final NavigableMap<Double, NavigableMap<String, Integer>> aggregatedEntries;

  /**
   * Creates a new PartWordExport object.
   * @param identifier the identifier of the export object
   * @param topEntries the collection of top entries
   * @param aggregatedEntries the collection of aggregated entries
   */
  private PartWordExport(String identifier,
      NavigableMap<Double, NavigableMap<String, Object>> topEntries,
      NavigableMap<Double, NavigableMap<String, Integer>> aggregatedEntries,
      ExportParams params) {
    super(identifier);
    if (params.hasDescendingEntries) {
      convertListEntriesToDescending(topEntries);
      convertListEntriesToDescending(aggregatedEntries);
    }
    this.topEntries = ExportService.checkDescending(topEntries, params.isDescending);
    this.aggregatedEntries = ExportService.checkDescending(aggregatedEntries, params.isDescending);
  }

  /**
   * Generates a new PartWordExport object based on an evaluator's results and
   * default export settings.
   * @param identifier the identifier of the export object to create
   * @param map the evaluator result
   * @return the generated PartWordExport object
   */
  public static PartWordExport create(String identifier, Map<String, Set<String>> map) {
    return create(identifier, map, ExportParams.builder().build(), new PartWordReducer.ByLength());
  }

  /**
   * Generates a new PartWordExport object based on an evaluator's results and the given settings.
   * @param identifier the identifier of the export object to create
   * @param results the evaluator results
   * @param params the export parameters
   * @param reducer the reducer to use to identify top entries
   * @return the generated PartWordExport object
   */
  public static PartWordExport create(String identifier, Map<String, Set<String>> results, 
      ExportParams params, PartWordReducer reducer) {
    NavigableMap<Double, NavigableMap<String, Set<String>>> orderedResults = 
        ExportService.applyGeneralMinimum(order(results, reducer), params.generalMinimum); 
    NavigableMap<Double, NavigableMap<String, Set<String>>> topEntries = 
        isolateTopEntries(orderedResults, params);

    NavigableMap<Double, NavigableMap<String, Integer>> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = aggregateEntries(orderedResults, params);
    } else {
      Double key = ExportService.getSmallestKey(topEntries);
      aggregatedEntries = aggregateEntries(orderedResults.headMap(key, false), params);
    }
    
    return new PartWordExport(identifier, trimTopEntries(topEntries, params), aggregatedEntries, params);
  }
  
  // Assign the reducer's computed relevance to the entries 
  private static NavigableMap<Double, NavigableMap<String, Set<String>>> order(
      Map<String, Set<String>> evaluatorResult, PartWordReducer reducer) {
    NavigableMap<Double, NavigableMap<String, Set<String>>> results = new TreeMap<>();
    for (Map.Entry<String, Set<String>> entry : evaluatorResult.entrySet()) {
      addEntryByRelevance(results, entry.getKey(), entry.getValue(), reducer);
    }
    return results;
  }

  // Creates aggregated entries - replaces the entries with the size
  private static NavigableMap<Double, NavigableMap<String, Integer>> aggregateEntries(
      NavigableMap<Double, NavigableMap<String, Set<String>>> results, ExportParams params) {
    NavigableMap<Double, NavigableMap<String, Integer>> aggregatedEntries = new TreeMap<>();
    for (Map.Entry<Double, NavigableMap<String, Set<String>>> entry : results.entrySet()) {
      aggregatedEntries.put(entry.getKey(), aggregateMap(entry.getValue(), params));
    }
    return aggregatedEntries;
  }
  
  // Trims the top entries list to conform to the export params' maxTopEntrySize setting
  private static NavigableMap<Double, NavigableMap<String, Object>> trimTopEntries(
      NavigableMap<Double, NavigableMap<String, Set<String>>> topEntries, ExportParams params) {    
    NavigableMap<Double, NavigableMap<String, Object>> result = new TreeMap<>();
    for (Map.Entry<Double, NavigableMap<String, Set<String>>> entry : topEntries.entrySet()) {
      result.put(entry.getKey(), trimTopEntriesSubMap(entry.getValue(), params));
    }
    return result;
  }
  
  // Helper method for order() to conveniently add an entry under its relevance
  private static void addEntryByRelevance(SortedMap<Double, NavigableMap<String, Set<String>>> results, String key, 
      Set<String> words, PartWordReducer reducer) {
    double relevance = reducer.computeRelevance(key, words);
    NavigableMap<String, Set<String>> entry = results.get(relevance);
    if (entry == null) {
      results.put(relevance, new TreeMap<>());
      entry = results.get(relevance);
    }
    entry.put(key, words);
  }
  
  private static <K, V> void convertListEntriesToDescending(
      NavigableMap<Double, NavigableMap<K, V>> map) {
    for (Map.Entry<Double, NavigableMap<K, V>> entry : map.entrySet()) {
      map.put(entry.getKey(), entry.getValue().descendingMap());
    }
  }
  
  private static NavigableMap<String, Object> trimTopEntriesSubMap(
      NavigableMap<String, Set<String>> subMap, ExportParams params) {
    NavigableMap<String, Object> result = new TreeMap<>();
    int addedEntries = 0;
    for (Map.Entry<String, Set<String>> entry : subMap.entrySet()) {
      if (params.maxTopEntrySize.isPresent() && addedEntries >= params.maxTopEntrySize.get()) {
        result.put(INDEX_REST, "" + Integer.toString(subMap.size() - addedEntries));
        break;
      }

      if (params.maxPartWordListSize.isPresent() && entry.getValue().size() > params.maxPartWordListSize.get()) {
        int initialSize = entry.getValue().size();
        List<String> words = reduceList(new ArrayList<String>(entry.getValue()), params.maxPartWordListSize.get());
        words.add(INDEX_REST + Integer.toString(initialSize - params.maxPartWordListSize.get()));
        result.put(entry.getKey(), words);
      } else {
        result.put(entry.getKey(), entry.getValue());
      }
      ++addedEntries;
    }
    return result;
  }

}