package ch.ljacqu.wordeval.evaluation.export;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Class representing the results of an evaluator in a format suitable for
 * exporting. An object typically has a collection of <i>top entries</i> which
 * are the most extreme/interesting elements the evaluator looks for. The other
 * entries are summed up and displayed as <i>aggregated entries</i>.
 */
public abstract class ExportObject implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * String used as special key to include the total of a group.
   */
  public static final String INDEX_TOTAL = "/total";
  /**
   * String used as special key to store the total number of entries that have
   * been trimmed.
   */
  public static final String INDEX_REST = "/rest";
  /**
   * The identifier of the export object (unique name per
   * evaluator/configuration).
   */
  public final String identifier;

  /**
   * Creates a new ExportObject instance.
   * @param identifier The identifier of the new object
   */
  public ExportObject(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Returns the input map trimmed to the biggest keys. The number of kept keys
   * depends on ExportParam's <code>topKeys</code> and <code>minimum</code>.
   * @param map The map to process
   * @param params The export parameters
   * @return The trimmed map with the top entries
   */
  protected static final <V> NavigableMap<Integer, V> getBiggestKeys(
      NavigableMap<Integer, V> map, ExportParams params) {
    Iterator<Integer> descendingIterator = map.descendingKeySet().iterator();
    Integer key = null;
    for (int i = 0; i < params.topKeys && descendingIterator.hasNext(); ++i) {
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

  /**
   * For maps with lists as values, it replaces the lists with their length
   * instead.
   * @param map The map to transform
   * @return The map with the original list's length
   */
  protected static final <K, V> NavigableMap<K, Integer> aggregateMap(
      NavigableMap<K, List<V>> map, ExportParams params) {
    NavigableMap<K, Integer> result = new TreeMap<>();
    for (Map.Entry<K, List<V>> entry : map.entrySet()) {
      result.put(entry.getKey(), entry.getValue().size());
    }
    if (params.isDescending) {
      return result.descendingMap();
    }
    return result;
  }

}
