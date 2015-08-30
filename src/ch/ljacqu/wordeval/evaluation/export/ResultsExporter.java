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

public class ResultsExporter {

  private static Gson gson;
  private static boolean DEBUG_MODE = true;

  public static String toJson(List<Evaluator> evaluators) {
    List<ExportObject> exportObjects = new ArrayList<>();
    for (Evaluator evaluator : evaluators) {
      exportObjects.add(evaluator.toExportObject());
    }
    return getGson().toJson(exportObjects);
  }

  public static void exportToFile(List<Evaluator> evaluators, String filename)
      throws IOException {
    String jsonOutput = toJson(evaluators);

    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(filename), "utf-8"))) {
      writer.write(jsonOutput);
    } catch (IOException e) {
      throw e; // ???
    }
  }

  private static Gson getGson() {
    if (gson == null) {
      if (DEBUG_MODE) {
        gson = new GsonBuilder().setPrettyPrinting().create();
      } else {
        gson = new Gson();
      }
    }
    return gson;
  }

}
