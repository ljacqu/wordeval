package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.evaluation.export.PartWordExport;
import ch.jalu.wordeval.evaluation.export.PartWordReducer;
import ch.jalu.wordeval.evaluation.export.ExportObject;

/**
 * Base class for part word evaluators. These evaluators save a part of the word
 * as the key in their results.
 */
public abstract class PartWordEvaluator extends DictionaryEvaluator<String> {

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
