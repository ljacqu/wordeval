package ch.ljacqu.wordeval.evaluation.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Aggregated version of an evaluator's results to export.
 * @param <K> The class the evaluator uses as key
 */
public final class PartWordExport extends ExportObject {

  private static final long serialVersionUID = 1L;

  private final SortedMap<Integer, SortedMap<String, String[]>> topEntries;
  private final SortedMap<Integer, SortedMap<String, Integer>> aggregatedEntries;

  public PartWordExport(String identifier,
      SortedMap<Integer, SortedMap<String, String[]>> topEntries,
      SortedMap<Integer, SortedMap<String, Integer>> aggregatedEntries) {
    super(identifier);
    this.topEntries = topEntries;
    this.aggregatedEntries = aggregatedEntries;
  }

  public SortedMap<Integer, SortedMap<String, String[]>> getTopEntries() {
    return topEntries;
  }

  public SortedMap<Integer, SortedMap<String, Integer>> getAggregatedEntries() {
    return aggregatedEntries;
  }

  public static PartWordExport createInstance(String identifier,
      int topLengths, NavigableMap<String, List<String>> map) {
    return createInstance(identifier, topLengths, map, null);
  }

  public static PartWordExport createInstance(String identifier,
      int topLengths, NavigableMap<String, List<String>> map, Integer minLength) {
    // {key: [words] ..} to {length: [{key: [words]}, ...]}
    NavigableMap<Integer, List<KeyAndWords>> entriesByLength = new TreeMap<>();
    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
      int length = entry.getKey().length();
      if (entriesByLength.get(length) == null) {
        entriesByLength.put(length, new ArrayList<>());
      }
      entriesByLength.get(length).add(new KeyAndWords(entry));
    }

    // Filter the top lengths in the new list
    SortedMap<Integer, List<KeyAndWords>> topEntries = getBiggestKeys(
        entriesByLength, topLengths);
    if (minLength != null) {
      if (topEntries.firstKey() < minLength) {
        topEntries = topEntries.tailMap(minLength);
      }
    }

    // Replace everything else from KeyAndWords to key: length
    SortedMap<Integer, SortedMap<String, Integer>> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = new TreeMap<>();
    } else {
      Integer key = topEntries.firstKey();
      aggregatedEntries = aggregateSmallerEntries(entriesByLength, key);
    }

    return new PartWordExport(identifier, keyAndWordsToMap(topEntries),
        aggregatedEntries);
  }

  private static SortedMap<Integer, SortedMap<String, Integer>> aggregateSmallerEntries(
      SortedMap<Integer, List<KeyAndWords>> map, int beforeKey) {
    SortedMap<Integer, SortedMap<String, Integer>> resultMap = new TreeMap<>();
    for (Map.Entry<Integer, List<KeyAndWords>> entry : map.headMap(beforeKey)
        .entrySet()) {
      int length = entry.getKey();
      resultMap.put(length, new TreeMap<>());
      for (KeyAndWords wordGroup : entry.getValue()) {
        resultMap.get(length).put(wordGroup.key, wordGroup.words.size());
      }
    }
    return resultMap;
  }

  private static SortedMap<Integer, SortedMap<String, String[]>> keyAndWordsToMap(
      SortedMap<Integer, List<KeyAndWords>> map) {
    SortedMap<Integer, SortedMap<String, String[]>> resultMap = new TreeMap<>();
    for (Map.Entry<Integer, List<KeyAndWords>> entry : map.entrySet()) {
      resultMap.put(entry.getKey(), new TreeMap<>());
      for (KeyAndWords keyAndWords : entry.getValue()) {
        String[] stringArray = new String[keyAndWords.words.size()];
        resultMap.get(entry.getKey()).put(keyAndWords.key,
            keyAndWords.words.toArray(stringArray));
      }
    }
    return resultMap;
  }

  public static class KeyAndWords {
    public final String key;
    public final List<String> words;

    public KeyAndWords(String key, List<String> words) {
      this.key = key;
      this.words = Collections.unmodifiableList(words);
    }

    public KeyAndWords(Map.Entry<String, List<String>> entry) {
      this(entry.getKey(), entry.getValue());
    }
  }

}
