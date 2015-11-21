package ch.ljacqu.wordeval.evaluation.export;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ch.ljacqu.wordeval.DataUtils;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import lombok.Getter;

/**
 * Service for the export of evaluator results.
 */
public final class ExportService {

  private static final boolean USE_PRETTY_PRINT = true;
  private static DataUtils dataUtils = new DataUtils(USE_PRETTY_PRINT);
  @Getter(lazy = true)
  private static final Gson gson = initGson();

  private ExportService() {
  }

  /**
   * Converts the results of the given evaluators to export objects in JSON
   * format.
   * @param evaluators the list of evaluators to process
   * @return the export data in JSON
   */
  private static String toJson(List<Evaluator<?>> evaluators) {
    return getGson().toJson(evaluators
        .stream()
        .map(Evaluator::toExportObject)
        .filter(Objects::nonNull)
        .collect(Collectors.toList()));
  }

  /**
   * Exports the results of the given evaluators to a file in JSON.
   * @param evaluators the list of evaluators to process
   * @param filename the name of the file to write the result to
   */
  public static void exportToFile(List<Evaluator<?>> evaluators, String filename) {
    String jsonOutput = toJson(evaluators);
    dataUtils.writeToFile(filename, jsonOutput);
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
   * @param <V> the value class of the map
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
   * Gets the smallest key present in a map.
   * @param <N> the key class of the map
   * @param <V> the value class of the map
   * @param map the map to process
   * @return the smallest key of the map
   */
  public static <N extends Number, V> N getSmallestKey(NavigableMap<N, V> map) {
    return Collections.reverseOrder().equals(map.comparator()) 
        ? map.lastKey() 
        : map.firstKey();
  }

  private static Gson initGson() {
    GsonBuilder builder = new GsonBuilder();
    final TreeElementSerializer serializer = new TreeElementSerializer();

    for (Class<?> clazz : TreeElement.class.getDeclaredClasses()) {
      if (TreeElement.class.isAssignableFrom(clazz)) {
        builder.registerTypeAdapter(clazz, serializer);
      }
    }
    return builder.create();
  }

  private static class TreeElementSerializer implements JsonSerializer<TreeElement> {
    @Override
    public JsonElement serialize(TreeElement src, Type typeOfSrc, JsonSerializationContext context) {
      return getGson().toJsonTree(src.getValue());
    }
  }

}
