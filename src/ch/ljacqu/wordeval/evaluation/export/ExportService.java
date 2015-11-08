package ch.ljacqu.wordeval.evaluation.export;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.ljacqu.wordeval.DataUtils;
import ch.ljacqu.wordeval.evaluation.Evaluator;

/**
 * Service for the export of evaluator results.
 */
public final class ExportService {

  private static final boolean USE_PRETTY_PRINT = true;
  private static DataUtils dataUtils = new DataUtils(USE_PRETTY_PRINT);

  private ExportService() {
  }

  /**
   * Converts the results of the given evaluators to export objects in JSON
   * format.
   * @param evaluators the list of evaluators to process
   * @return the export data in JSON
   */
  private static String toJson(List<Evaluator<?>> evaluators) {
    return dataUtils.toJson(evaluators
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

}
