package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;
import ch.ljacqu.wordeval.evaluation.export.PartWordExport;
import ch.ljacqu.wordeval.evaluation.export.PartWordReducer;

public abstract class PartWordEvaluator extends Evaluator<String> {

  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    if (params == null) {
      return PartWordExport.create(identifier, getResults());
    }
    return PartWordExport.create(identifier, getResults(), params, new PartWordReducer.ByLength());
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
