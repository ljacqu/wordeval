package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;

/**
 * Base class for part word evaluators. These evaluators save a part of the word
 * as the key in their results.
 */
@Deprecated
public abstract class PartWordEvaluator extends DictionaryEvaluator<String> {

  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    return null;
  }

}
