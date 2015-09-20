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

  private static final long serialVersionUID = 1L;

  private final NavigableMap<Number, NavigableMap<String, Object>> topEntries;
  private final NavigableMap<Number, NavigableMap<String, Integer>> aggregatedEntries;

  /**
   * Creates a new PartWordExport object.
   * @param identifier The identifier of the export object
   * @param topEntries The collection of top entries
   * @param aggregatedEntries The collection of aggregated entries
   */
  public PartWordExport(String identifier,
      NavigableMap<Number, NavigableMap<String, Object>> topEntries,
      NavigableMap<Number, NavigableMap<String, Integer>> aggregatedEntries) {
    super(identifier);
    this.topEntries = topEntries;
    this.aggregatedEntries = aggregatedEntries;
  }

  /**
   * Generates a new PartWordExport object based on an evaluator's results and
   * default export settings.
   * @param identifier The identifier of the export object to create
   * @param map The evaluator result
   * @return The generated PartWordExport object
   */
  public static PartWordExport create(String identifier, Map<String, Set<String>> map) {
    return create(identifier, map, new ExportParamsBuilder().build(), new PartWordReducer.ByLength());
  }

  /**
   * Generates a new PartWordExport object based on an evaluator's results and the given settings.
   * @param identifier The identifier of the export object to create
   * @param map The evaluator result
   * @param params The export parameters
   * @param reducer The reducer to use to identify top entries
   * @return The generated PartWordExport object
   */
  public static PartWordExport create(String identifier, Map<String, Set<String>> map, 
      ExportParams params, PartWordReducer reducer) {    
    NavigableMap<Number, NavigableMap<String, Set<String>>> orderedResults = order(map, reducer);
    NavigableMap<Number, NavigableMap<String, Set<String>>> topEntries = isolateTopEntries(orderedResults, params);

    NavigableMap<Number, NavigableMap<String, Integer>> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = aggregateEntries(orderedResults, params);
    } else {
      Number key = getBiggestKey(topEntries);
      aggregatedEntries = aggregateEntries(orderedResults.headMap(key, false), params);
    }
    
    return new PartWordExport(identifier, trimTopEntries(topEntries, params), aggregatedEntries);
  }
  
  // Assign the reducer's computed relevance to the entries 
  private static NavigableMap<Number, NavigableMap<String, Set<String>>> order(
      Map<String, Set<String>> evaluatorResult, PartWordReducer reducer) {
    NavigableMap<Number, NavigableMap<String, Set<String>>> results = new TreeMap<>();
    for (Map.Entry<String, Set<String>> entry : evaluatorResult.entrySet()) {
      addEntryByRelevance(results, entry.getKey(), entry.getValue(), reducer);
    }
    return results;
  }

  // Creates aggregated entries - replaces the entries with the size
  private static NavigableMap<Number, NavigableMap<String, Integer>> aggregateEntries(
      NavigableMap<Number, NavigableMap<String, Set<String>>> results, ExportParams params) {
    NavigableMap<Number, NavigableMap<String, Integer>> aggregatedEntries = new TreeMap<>();
    for (Map.Entry<Number, NavigableMap<String, Set<String>>> entry : results.entrySet()) {
      aggregatedEntries.put(entry.getKey(), aggregateMap(entry.getValue(), params));
    }
    return checkDescending(aggregatedEntries, params);
  }
  
  // Trims the top entries list to conform to the export params' maxTopEntrySize setting
  private static NavigableMap<Number, NavigableMap<String, Object>> trimTopEntries(
      NavigableMap<Number, NavigableMap<String, Set<String>>> topEntries, ExportParams params) {    
    NavigableMap<Number, NavigableMap<String, Object>> result = new TreeMap<>();
    for (Map.Entry<Number, NavigableMap<String, Set<String>>> entry : topEntries.entrySet()) {
      result.put(entry.getKey(), trimTopEntriesSubMap(entry.getValue(), params));
    }
    return checkDescending(result, params);
  }
  
  // Helper method for order() to conveniently add an entry under its relevance
  private static void addEntryByRelevance(SortedMap<Number, NavigableMap<String, Set<String>>> results, String key, 
      Set<String> words, PartWordReducer reducer) {
    Number relevance = reducer.computeRelevance(key, words);
    NavigableMap<String, Set<String>> entry = results.get(relevance);
    if (entry == null) {
      results.put(relevance, new TreeMap<>());
      entry = results.get(relevance);
    }
    entry.put(key, words);
  }
  
  private static NavigableMap<String, Object> trimTopEntriesSubMap(
      NavigableMap<String, Set<String>> subMap, ExportParams params) {
    NavigableMap<String, Object> result = new TreeMap<>();
    int addedEntries = 0;
    for (Map.Entry<String, Set<String>> entry : subMap.entrySet()) {
      if (params.maxTopEntrySize != null && addedEntries >= params.maxTopEntrySize) {
        result.put(INDEX_REST, "" + (subMap.size() - addedEntries));
        break;
      }
      
      if (params.maxPartWordListSize != null && entry.getValue().size() > params.maxPartWordListSize) {
        int initialSize = entry.getValue().size();
        List<String> words = reduceList(new ArrayList<>(entry.getValue()), params.maxPartWordListSize);
        words.add(INDEX_REST + (initialSize - params.maxPartWordListSize));
        result.put(entry.getKey(), words);
      } else {
        result.put(entry.getKey(), entry.getValue());
      }
      ++addedEntries;
    }
    return result;
  }

}