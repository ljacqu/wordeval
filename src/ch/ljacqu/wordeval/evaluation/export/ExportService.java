package ch.ljacqu.wordeval.evaluation.export;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Service for the export of evaluator results.
 */
public final class ExportService {

  private static final boolean USE_PRETTY_PRINT = true;
  private static Gson gson;

  private ExportService() {
  }

  /**
   * Converts the results of the given evaluators to export objects in JSON
   * format.
   * @param evaluators The list of evaluators to process
   * @return The export data in JSON
   */
  public static String toJson(List<Evaluator<?>> evaluators) {
    List<ExportObject> exportObjects = new ArrayList<>();
    for (Evaluator<?> evaluator : evaluators) {
      exportObjects.add(evaluator.toExportObject());
    }
    return getGson().toJson(exportObjects);
  }

  /**
   * Exports the results of the given evaluators to a file in JSON.
   * @param evaluators The list of evaluators to process
   * @param filename The name of the file to write the result to
   * @throws IOException If the file cannot be opened or written to
   */
  public static void exportToFile(List<Evaluator<?>> evaluators, String filename) throws IOException {
    String jsonOutput = toJson(evaluators);

    Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
    writer.write(jsonOutput);
    writer.close();
  }

  private static Gson getGson() {
    if (gson == null) {
      if (USE_PRETTY_PRINT) {
        gson = new GsonBuilder().setPrettyPrinting().create();
      } else {
        gson = new Gson();
      }
    }
    return gson;
  }

}