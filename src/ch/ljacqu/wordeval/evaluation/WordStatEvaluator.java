package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;
import ch.ljacqu.wordeval.evaluation.export.WordStatExport;

public abstract class WordStatEvaluator extends Evaluator<Integer> {

  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    if (params == null) {
      return WordStatExport.create(identifier, results);
    }
    return WordStatExport.create(identifier, results, params);
  }

  /**
   * Creates an export object with the given export parameters.
   * @param params The export parameters to use
   * @return The generated export object
   */
  protected ExportObject toExportObject(ExportParams params) {
    return toExportObject(this.getClass().getSimpleName(), params);
  }

}
