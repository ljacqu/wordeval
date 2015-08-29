package ch.ljacqu.wordeval.evaluation.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Aggregated version of an evaluator's results to export.
 * @param <K> The class the evaluator uses as key
 */
public final class StringExportResult extends ExportResult {

  private static final long serialVersionUID = 1L;

  private final String identifier;
  private final SortedMap<Integer, List<KeyAndWords>> topEntries;
  private final SortedMap<Integer, List<KeyAndTotal>> aggregatedEntries;

  public StringExportResult(String identifier,
      SortedMap<Integer, List<KeyAndWords>> topEntries,
      SortedMap<Integer, List<KeyAndTotal>> aggregatedEntries) {
    this.identifier = identifier;
    this.topEntries = topEntries;
    this.aggregatedEntries = aggregatedEntries;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String toString() {
    return "ExportResult [identifier=" + identifier + ", topEntries="
        + topEntries + ", aggregatedEntries=" + aggregatedEntries + "]";
  }

  public static StringExportResult createInstance(String identifier,
      int topLengths, NavigableMap<String, List<String>> map) {
    // Replace {key: [words], ...} to {length: [{key: words}, {key: words}],
    // ...}
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

    // Replace everything else from KeyAndWords to KeyAndLength
    SortedMap<Integer, List<KeyAndTotal>> aggregatedEntries;
    if (topEntries.isEmpty()) {
      aggregatedEntries = new TreeMap<>();
    } else {
      Integer key = topEntries.firstKey();
      aggregatedEntries = aggregateSmallerEntries(entriesByLength, key);
    }

    return new StringExportResult(identifier, topEntries, aggregatedEntries);
  }

  private static SortedMap<Integer, List<KeyAndTotal>> aggregateSmallerEntries(
      SortedMap<Integer, List<KeyAndWords>> map, int beforeKey) {
    SortedMap<Integer, List<KeyAndTotal>> resultMap = new TreeMap<>();
    for (Map.Entry<Integer, List<KeyAndWords>> entry : map.headMap(beforeKey)
        .entrySet()) {
      int length = entry.getKey();
      resultMap.put(length, new ArrayList<KeyAndTotal>());
      for (KeyAndWords wordGroup : entry.getValue()) {
        resultMap.get(length).add(wordGroup.toKeyAndLength());
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

    public KeyAndWords(Entry<String, List<String>> entry) {
      this(entry.getKey(), entry.getValue());
    }

    public KeyAndTotal toKeyAndLength() {
      return new KeyAndTotal(key, words.size());
    }
    
    @Override
    public String toString() {
      return "{" + key + ": " + words + "}";
    }
  }

  public static class KeyAndTotal {
    public final String key;
    public final int total;

    public KeyAndTotal(String key, int total) {
      this.key = key;
      this.total = total;
    }
    
    @Override
    public String toString() {
      return "{" + key + ": " + total + "}";
    }
  }

}
