package ch.ljacqu.wordeval.evaluation.export;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Class representing the results of an evaluator in a format suitable for
 * exporting.
 */
public abstract class ExportObject implements Serializable {

  public static final String INDEX_TOTAL = "/total";
  public static final String INDEX_REST = "/rest";
  private static final long serialVersionUID = 1L;
  public final String identifier;

  public ExportObject(String identifier) {
    this.identifier = identifier;
  }

  protected static final <V> NavigableMap<Integer, V> getBiggestKeys(
      NavigableMap<Integer, V> map, ExportParams params) {
    Iterator<Integer> descendingIterator = map.descendingKeySet().iterator();
    Integer key = null;
    for (int i = 0; i < params.number && descendingIterator.hasNext(); ++i) {
      key = descendingIterator.next();
      if (params.minimum != null && key < params.minimum) {
        key = params.minimum;
        break;
      }
    }
    if (key != null) {
      NavigableMap<Integer, V> resultsMap = map.tailMap(key, true);
      if (params.isDescending) {
        return resultsMap.descendingMap();
      }
      return resultsMap;
    }
    return new TreeMap<>();
  }

  protected static final <K, V> NavigableMap<K, Integer> computeAggregatedMap(
      NavigableMap<K, List<V>> map, K toKey, ExportParams params) {
    NavigableMap<K, List<V>> headMap = map.headMap(toKey, false);
    NavigableMap<K, Integer> resultMap;
    if (params.isDescending) {
      resultMap = new TreeMap<>(Collections.reverseOrder());
    } else {
      resultMap = new TreeMap<>();
    }
    for (Map.Entry<K, List<V>> entry : headMap.entrySet()) {
      resultMap.put(entry.getKey(), entry.getValue().size());
    }
    return resultMap;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [identifier=" + identifier + "]";
  }
}
