package ch.ljacqu.wordeval.evaluation.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.ljacqu.wordeval.evaluation.Evaluator;
import lombok.Getter;

/**
 * Service for the export of evaluator results.
 */
public final class ExportService {

  private static final boolean USE_PRETTY_PRINT = true;

  @Getter(lazy = true)
  private static final Gson gson = createGson();

  private ExportService() {
  }

  /**
   * Converts the results of the given evaluators to export objects in JSON
   * format.
   * @param evaluators the list of evaluators to process
   * @return the export data in JSON
   */
  public static String toJson(List<Evaluator<?>> evaluators) {
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

    try {
      Files.write(Paths.get(filename), jsonOutput.getBytes());
    } catch (IOException e) {
      throw new IllegalStateException("Could not write to file", e);
    }
  }

  private static Gson createGson() {    
    if (USE_PRETTY_PRINT) {
      return new GsonBuilder().setPrettyPrinting().create();
    }
    return new Gson();
  }

}
