package ch.ljacqu.wordeval.evaluation.export;

import ch.ljacqu.wordeval.DataUtils;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for the export and generation of export objects.
 */
public final class ExportService {

  private static final boolean USE_PRETTY_PRINT = true;
  private static DataUtils dataUtils = new DataUtils();
  @Getter(lazy = true)
  private static final Gson gson = initGson(USE_PRETTY_PRINT);

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

  /**
   * Initializes a GSON instance and registers the custom serializer
   * for implementations of {@link TreeElement}.
   * @return the built GSON instance
   */
  private static Gson initGson(boolean USE_PRETTY_PRINT) {
    GsonBuilder builder = new GsonBuilder();
    if (USE_PRETTY_PRINT) {
      builder.setPrettyPrinting();
    }
    final TreeElementSerializer serializer = new TreeElementSerializer();

    for (Class<?> clazz : TreeElement.class.getDeclaredClasses()) {
      if (TreeElement.class.isAssignableFrom(clazz)) {
        builder.registerTypeAdapter(clazz, serializer);
      }
    }
    return builder.create();
  }

  /**
   * Custom serializer for {@link TreeElement} instances to only serialize the actual
   * value they contain, rather than the entire <code>"value": {...}</code> field in
   * the JSON output.
   */
  private static class TreeElementSerializer implements JsonSerializer<TreeElement> {
    @Override
    public JsonElement serialize(TreeElement src, Type typeOfSrc, JsonSerializationContext context) {
      return getGson().toJsonTree(src.getValue());
    }
  }

}
