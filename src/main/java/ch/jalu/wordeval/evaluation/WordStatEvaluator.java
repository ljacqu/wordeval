package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.evaluation.export.WordStatExport;

/**
 * Base class for the word stat evaluator - evaluator that saves some figure
 * with a word (typically its length).
 */
public abstract class WordStatEvaluator extends DictionaryEvaluator<Integer> {

  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    if (params == null) {
      return WordStatExport.create(identifier, getNavigableResults());
    }
    return WordStatExport.create(identifier, getNavigableResults(), params);
  }

  /**
   * Creates an export object with the given export parameters.
   *
   * @param params The export parameters to use
   * @return The generated export object
   */
  protected ExportObject toExportObject(ExportParams params) {
    return toExportObject(this.getClass().getSimpleName(), params);
  }

}
