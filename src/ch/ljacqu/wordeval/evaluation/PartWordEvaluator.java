package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.PartWordExport;

public abstract class PartWordEvaluator extends Evaluator<String> {

  @Override
  protected ExportObject toExportObject(String identifier, Integer topEntries) {
    return PartWordExport.create(identifier, results);
  }

}
