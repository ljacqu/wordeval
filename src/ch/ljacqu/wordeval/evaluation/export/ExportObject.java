package ch.ljacqu.wordeval.evaluation.export;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.SortedMap;
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
   * The identifier of the export object (unique name per evaluator/configuration).
   */
  public final String identifier;

  private static Random random = new Random();

  /**
   * Creates a new ExportObject instance.
   * @param identifier The identifier of the new object
   */
  ExportObject(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Returns the input map trimmed to the biggest keys. The number of kept keys
   * depends on ExportParam's <code>topKeys</code> and <code>minimum</code>.
   * @param map The map to process
   * @param params The export parameters
   * @param <N> The specific Number subclass for the relevance (Integer or Double)
   * @param <V> The value class of the given Map
   * @return The trimmed map with the top entries
   */
  protected static final <N extends Number, V> NavigableMap<N, V> isolateTopEntries(NavigableMap<N, V> map, 
      ExportParams params) {
    Iterator<N> descendingIterator = map.descendingKeySet().iterator();
    N key = null;
    for (int i = 0; i < params.topKeys && descendingIterator.hasNext(); ++i) {
      key = descendingIterator.next();
      if (params.minimum >= 0 && key.doubleValue() < params.minimum) {
        key = returnTypedMinimum(key, params);
        break;
      }
    }
    if (key != null) {
      NavigableMap<N, V> resultsMap = map.tailMap(key, true);
      return checkDescending(resultsMap, params);
    }
    return new TreeMap<>();
  }

  @SuppressWarnings("unchecked")
  private static <N extends Number> N returnTypedMinimum(N key, ExportParams params) {
    if (key instanceof Integer) {
      // Compiler doesn't understand that N == Integer, so we need to "cast"
      return (N) Integer.valueOf((int) params.minimum);
    } else if (key instanceof Double) {
      return (N) Double.valueOf(params.minimum);
    }
    throw new IllegalStateException("Key is neither integer nor double!");
  }

  /**
   * For maps with a collection or a map as values, it replaces the lists with
   * their length instead.
   * @param map The map to transform
   * @param params The export parameters
   * @param <K> The key class of the input map
   * @param <V> The value class of the map (Collection or Map)
   * @return The map with the original list's length
   */
  protected static final <K, V> NavigableMap<K, Integer> aggregateMap(
      NavigableMap<K, V> map, ExportParams params) {
    NavigableMap<K, Integer> result = new TreeMap<>();
    for (Map.Entry<K, V> entry : map.entrySet()) {
      if (entry.getValue() instanceof Collection) {
        result.put(entry.getKey(), ((Collection<?>) entry.getValue()).size());
      } else if (entry.getValue() instanceof Map) {
        result.put(entry.getKey(), ((Map<?, ?>) entry.getValue()).size());
      } else {
        throw new IllegalStateException("Entry is neither Collection nor Map");
      }
    }
    return checkDescending(result, params);
  }

  protected static final <T> List<T> reduceList(List<T> words, int toSize) {
    int size = words.size();
    if (size <= toSize) {
      return words;
    }

    int delta = Math.max(size / toSize, 1);
    int key = random.nextInt(delta);
    List<T> result = new ArrayList<>();
    while (result.size() < toSize) {
      result.add(words.get(key));
      --size;
      key += delta;
    }
    return result;
  }

  protected static final <K, V> NavigableMap<K, V> checkDescending(NavigableMap<K, V> map, ExportParams params) {
    if (params.isDescending) {
      return map.descendingMap();
    }
    return map;
  }

  protected static final <K, V> K getBiggestKey(SortedMap<K, V> map) {
    return Collections.reverseOrder().equals(map.comparator()) ? map.lastKey() : map.firstKey();
  }

}