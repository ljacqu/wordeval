package ch.jalu.wordeval.evaluation.export;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;

/**
 * Service for creating the data in {@link ExportObject} implementations.
 */
public final class ExportObjectService {

  private static Random random = new Random();

  private ExportObjectService() {
  }

  /**
   * Method to make a map descending if necessary.
   * @param <K> the key class of the map
   * @param <V> the value class of the map
   * @param map the map to potentially change
   * @param isDescending whether or not to convert to a descending map
   * @return the map, descending if necessary
   */
  public static <K, V> NavigableMap<K, V> checkDescending(
      NavigableMap<K, V> map, boolean isDescending) {
    return isDescending ? map.descendingMap() : map;
  }

  /**
   * Removes the entries that are below {@link ExportParams#generalMinimum}
   * if a value is present.
   * @param <N> the key class of the map
   * @param map the map to reduce
   * @param minimum the minimum value
   * @return the map conforming to the general minimum
   */
  public static <N extends Number, V> NavigableMap<N, V> applyGeneralMinimum(
      NavigableMap<N, V> map, Optional<N> minimum) {
    return minimum.isPresent()
        ? map.tailMap(minimum.get(), true)
        : map;
  }

  /**
   * Returns the input map trimmed to the biggest keys. The number of kept keys
   * depends on ExportParam's <code>topKeys</code> and <code>minimum</code>.
   * @param map the map to process
   * @param params the export parameters
   * @param <N> the specific Number subclass for the relevance (Integer or Double)
   * @param <V> the value class of the given Map
   * @return the trimmed map with the top entries
   */
  public static <N extends Number, V> NavigableMap<N, V> isolateTopEntries(
      NavigableMap<N, V> map, ExportParams params) {
    Iterator<N> descendingIterator = map.descendingKeySet().iterator();
    N key = null;
    for (int i = 0; i < params.topKeys && descendingIterator.hasNext(); ++i) {
      key = descendingIterator.next();
      if (params.topEntryMinimum.isPresent() && key.doubleValue() < params.topEntryMinimum.get()) {
        key = getTypedMinimum(params.topEntryMinimum.get(), key);
        break;
      }
    }
    if (key != null) {
      return map.tailMap(key, true);
    }
    return new TreeMap<>();
  }

  @SuppressWarnings("unchecked")
  private static <N extends Number> N getTypedMinimum(double value, N key) {
    if (key instanceof Integer) {
      return (N) Integer.valueOf((int) value);
    } else if (key instanceof Double) {
      return (N) Double.valueOf(value);
    }
    throw new IllegalStateException("Number is neither Integer nor Double");
  }

  /**
   * For maps with a collection or a map as values, it replaces the lists with
   * their length instead.
   * @param map the map to transform
   * @param <K> the key class of the input map
   * @param <V> the value class of the map (Collection or Map)
   * @return the map with the original list's length
   */
  public static <K, V> NavigableMap<K, Integer> aggregateMap(Map<K, V> map) {
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
    return result;
  }

  public static <K, V> NavigableMap<K, Integer> aggregateMap(Multimap<K, V> multimap) {
    return aggregateMap(multimap.asMap());
  }

  public static <T> List<T> reduceList(List<T> words, int toSize) {
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

}
