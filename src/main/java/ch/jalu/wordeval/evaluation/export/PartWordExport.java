package ch.jalu.wordeval.evaluation.export;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * ExportObject class for evaluators of type PartWordEvaluator.
 */
@Getter
public final class PartWordExport extends ExportObject {

  /**
   * Creates a new PartWordExport object.
   * @param identifier the identifier of the export object
   * @param topEntries the collection of top entries
   * @param aggregatedEntries the collection of aggregated entries
   * @param params the export parameters
   */
  private PartWordExport(String identifier,
      NavigableMap<Double, NavigableMap<String, TreeElement>> topEntries,
      NavigableMap<Double, TreeElement> aggregatedEntries,
      ExportParams params) {
    super(identifier,
      applyDescendingParamsToTopEntries(topEntries, params),
      applyDescendingParamsToAggrEntries(aggregatedEntries, params));
  }

  @Override
  public NavigableMap<Double, NavigableMap<String, TreeElement>> getTopEntries() {
    return (NavigableMap<Double, NavigableMap<String, TreeElement>>) super.getTopEntries();
  }

  @Override
  public NavigableMap<Double, TreeElement> getAggregatedEntries() {
    return (NavigableMap<Double, TreeElement>) super.getAggregatedEntries();
  }

  /**
   * Generates a new PartWordExport object based on an evaluator's results and
   * default export settings.
   * @param identifier the identifier of the export object to create
   * @param map the evaluator result
   * @return the generated PartWordExport object
   */
  public static PartWordExport create(String identifier, Multimap<String, String> map) {
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
  public static PartWordExport create(String identifier, Multimap<String, String> results,
                                      ExportParams params, PartWordReducer reducer) {
    NavigableMap<Double, Multimap<String, String>> orderedResults =
        ExportObjectService.applyGeneralMinimum(order(results, reducer), params.generalMinimum);
    NavigableMap<Double, Multimap<String, String>> topEntries =
        ExportObjectService.isolateTopEntries(orderedResults, params);

    NavigableMap<Double, TreeElement> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = aggregateEntries(orderedResults, params);
    } else {
      Double key = ExportService.getSmallestKey(topEntries);
      aggregatedEntries = aggregateEntries(orderedResults.headMap(key, false), params);
    }
    
    return new PartWordExport(identifier, trimTopEntries(topEntries, params), aggregatedEntries, params);
  }
  
  // Assign the reducer's computed relevance to the entries 
  private static NavigableMap<Double, Multimap<String, String>> order(
      Multimap<String, String> evaluatorResult, PartWordReducer reducer) {
    NavigableMap<Double, Multimap<String, String>> results = new TreeMap<>();
    for (Map.Entry<String, Collection<String>> entry : evaluatorResult.asMap().entrySet()) {
      addEntryByRelevance(results, entry.getKey(), entry.getValue(), reducer);
    }
    return results;
  }

  // Creates aggregated entries - replaces the entries with the size
  private static NavigableMap<Double, TreeElement> aggregateEntries(
      NavigableMap<Double, Multimap<String, String>> results, ExportParams params) {
    NavigableMap<Double, TreeElement> aggregatedEntries = new TreeMap<>();
    int fullAggregated = 0;
    for (Map.Entry<Double, Multimap<String, String>> entry : results.descendingMap().entrySet()) {
      if (params.numberOfDetailedAggregation.isPresent() 
          && fullAggregated >= params.numberOfDetailedAggregation.get()) {
        aggregatedEntries.put(entry.getKey(), totalOfMap(entry.getValue()));
      } else {
        TreeElement indexTotal = new TreeElement.IndexTotalColl(ExportObjectService.aggregateMap(entry.getValue()));
        aggregatedEntries.put(entry.getKey(), indexTotal);
        ++fullAggregated;
      }
    }
    return aggregatedEntries;
  }
  
  private static TreeElement.Total totalOfMap(Multimap<String, String> map) {
    return new TreeElement.Total(map.size());
  }
  
  // Trims the top entries list to conform to the export params' maxTopEntrySize setting
  private static NavigableMap<Double, NavigableMap<String, TreeElement>> trimTopEntries(
      NavigableMap<Double, Multimap<String, String>> topEntries, ExportParams params) {
    NavigableMap<Double, NavigableMap<String, TreeElement>> result = new TreeMap<>();
    for (Map.Entry<Double, Multimap<String, String>> entry : topEntries.entrySet()) {
      result.put(entry.getKey(), trimTopEntriesSubMap(entry.getValue(), params));
    }
    return result;
  }
  
  // Helper method for order() to conveniently add an entry under its relevance
  private static void addEntryByRelevance(SortedMap<Double, Multimap<String, String>> results, String key,
      Collection<String> words, PartWordReducer reducer) {
    double relevance = reducer.computeRelevance(key, words);
    Multimap<String, String> entry = results.get(relevance);
    if (entry == null) {
      entry = TreeMultimap.create();
      results.put(relevance, entry);
    }
    entry.putAll(key, words);
  }
  
  private static <K, V> NavigableMap<Double, NavigableMap<K, V>> applyDescendingParamsToTopEntries(
      NavigableMap<Double, NavigableMap<K, V>> map, ExportParams params) {
    if (params.hasDescendingEntries) {
      for (Map.Entry<Double, NavigableMap<K, V>> entry : map.entrySet()) {
        map.put(entry.getKey(), entry.getValue().descendingMap());
      }
    }
    return ExportObjectService.checkDescending(map, params.isDescending);
  }
  
  private static NavigableMap<Double, TreeElement> applyDescendingParamsToAggrEntries(
      NavigableMap<Double, TreeElement> map, ExportParams params) {
    if (params.hasDescendingEntries) {
      for (Map.Entry<Double, TreeElement> entry : map.entrySet()) {
        if (entry.getValue() instanceof TreeElement.IndexTotalColl) {
          NavigableMap<String, Integer> reversedMap = ((TreeElement.IndexTotalColl) entry.getValue())
              .getTypedValue().descendingMap();
          map.put(entry.getKey(), new TreeElement.IndexTotalColl(reversedMap));
        }
      }
    }
    return ExportObjectService.checkDescending(map, params.isDescending);
  }
  
  private static NavigableMap<String, TreeElement> trimTopEntriesSubMap(
      Multimap<String, String> subMap, ExportParams params) {
    NavigableMap<String, TreeElement> result = new TreeMap<>();
    int addedEntries = 0;
    for (Map.Entry<String, Collection<String>> entry : subMap.asMap().entrySet()) {
      if (params.maxTopEntrySize.isPresent() && addedEntries >= params.maxTopEntrySize.get()) {
        result.put(INDEX_REST, new TreeElement.Rest(subMap.keySet().size() - addedEntries));
        break;
      }

      if (params.maxPartWordListSize.isPresent() && entry.getValue().size() > params.maxPartWordListSize.get()) {
        int initialSize = entry.getValue().size();
        List<String> words = ExportObjectService.
            reduceList(new ArrayList<>(entry.getValue()), params.maxPartWordListSize.get());
        words.add(INDEX_REST + Integer.toString(initialSize - params.maxPartWordListSize.get()));
        result.put(entry.getKey(), new TreeElement.WordColl(words));
      } else {
        result.put(entry.getKey(), new TreeElement.WordColl(entry.getValue()));
      }
      ++addedEntries;
    }
    return result;
  }

}