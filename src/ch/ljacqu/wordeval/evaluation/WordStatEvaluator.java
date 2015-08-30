package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.WordStatExport;

public abstract class WordStatEvaluator extends Evaluator<Integer> {

  @Override
  protected ExportObject toExportObject(String identifier, int topEntries) {
    return WordStatExport.create(identifier, topEntries, results);
  }

}
