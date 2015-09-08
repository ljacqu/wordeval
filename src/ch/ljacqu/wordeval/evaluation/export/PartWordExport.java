package ch.ljacqu.wordeval.evaluation.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * ExportObject class for evaluators of type PartWordEvaluator.
 */
public final class PartWordExport extends ExportObject {

  private static final long serialVersionUID = 1L;

  private final SortedMap<Integer, SortedMap<String, Object>> topEntries;
  private final SortedMap<Integer, SortedMap<String, Integer>> aggregatedEntries;

  public PartWordExport(String identifier,
      SortedMap<Integer, SortedMap<String, Object>> topEntries,
      SortedMap<Integer, SortedMap<String, Integer>> aggregatedEntries) {
    super(identifier);
    this.topEntries = topEntries;
    this.aggregatedEntries = aggregatedEntries;
  }

  public SortedMap<Integer, SortedMap<String, Object>> getTopEntries() {
    return topEntries;
  }

  public SortedMap<Integer, SortedMap<String, Integer>> getAggregatedEntries() {
    return aggregatedEntries;
  }

  public static PartWordExport create(String identifier,
      Map<String, Set<String>> map) {
    return create(identifier, map, new ExportParamsBuilder().build());
  }

  public static PartWordExport create(String identifier,
      Map<String, Set<String>> map, ExportParams params) {
    // {key: [words] ..} to {length: [{key: [words]}, ...]}
    NavigableMap<Integer, List<KeyAndWords>> entriesByLength = groupByLength(
        map, params);

    // Filter the top lengths in the new list
    SortedMap<Integer, List<KeyAndWords>> topEntries = isolateTopEntries(
        entriesByLength, params);

    // Replace everything else from KeyAndWords to key: length
    SortedMap<Integer, SortedMap<String, Integer>> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = aggregateSmallerEntries(entriesByLength, params);
    } else {
      int key = getBiggestKey(topEntries);
      aggregatedEntries = aggregateSmallerEntries(
          entriesByLength.headMap(key, false), params);
    }

    return new PartWordExport(identifier, trimTopEntries(topEntries, params),
        aggregatedEntries);
  }

  private static SortedMap<Integer, SortedMap<String, Integer>> aggregateSmallerEntries(
      SortedMap<Integer, List<KeyAndWords>> map, ExportParams params) {
    NavigableMap<Integer, SortedMap<String, Integer>> result = new TreeMap<>();
    for (Map.Entry<Integer, List<KeyAndWords>> entry : map.entrySet()) {
      int length = entry.getKey();
      result.put(length, new TreeMap<>());
      for (KeyAndWords wordGroup : entry.getValue()) {
        result.get(length).put(wordGroup.key, wordGroup.words.size());
      }
    }
    return checkDescending(result, params);
  }

  private static NavigableMap<Integer, List<KeyAndWords>> groupByLength(
      Map<String, Set<String>> map, ExportParams params) {
    NavigableMap<Integer, List<KeyAndWords>> result = new TreeMap<>();
    for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
      int length = entry.getKey().length();
      if (result.get(length) == null) {
        result.put(length, new ArrayList<>());
      }
      result.get(length).add(new KeyAndWords(entry));
    }
    return result;
  }

  private static SortedMap<Integer, SortedMap<String, Object>> trimTopEntries(
      SortedMap<Integer, List<KeyAndWords>> map, ExportParams params) {
    NavigableMap<Integer, SortedMap<String, Object>> result = new TreeMap<>();
    for (Map.Entry<Integer, List<KeyAndWords>> entry : map.entrySet()) {
      result.put(entry.getKey(), trimKeyAndWordsList(entry.getValue(), params));
    }
    return checkDescending(result, params);
  }

  /**
   * Trims the number of text-keys as well as the number of entries within such
   * a text key as per the export params. Replaces KeyAndWords to string array.
   * @param list The list to process
   * @param params The export parameters
   * @return A list of converted and trimmed entries
   */
  private static SortedMap<String, Object> trimKeyAndWordsList(
      List<KeyAndWords> list, ExportParams params) {
    int restSize = 0;
    if (params.maxTopEntrySize != null && list.size() > params.maxTopEntrySize) {
      restSize = list.size() - params.maxTopEntrySize;
      list = list.subList(0, params.maxTopEntrySize);
    }

    // Object instead of String[] allows us to add the rest size simply as value
    // This still exports just fine as Json
    SortedMap<String, Object> result = new TreeMap<>();
    for (KeyAndWords kaw : list) {
      result.put(kaw.key, kaw.getMergedWordsArray(params.maxPartWordListSize));
    }
    if (restSize > 0) {
      result.put(ExportObject.INDEX_REST, restSize);
    }
    return result;
  }

  private static class KeyAndWords {
    public final String key;
    public final List<String> words;

    public KeyAndWords(Map.Entry<String, Set<String>> entry) {
      key = entry.getKey();
      words = new ArrayList<>(entry.getValue());
    }

    public String[] getMergedWordsArray(Integer maxSize) {
      List<String> words = this.words;
      if (maxSize != null && words.size() > maxSize) {
        int restSize = words.size() - maxSize;
        words = words.subList(0, maxSize);
        words.add(ExportObject.INDEX_REST + restSize);
      }
      return words.toArray(new String[words.size()]);
    }
  }

}