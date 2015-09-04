package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParamsBuilder;
import ch.ljacqu.wordeval.evaluation.export.WordStatExport;

public abstract class WordStatEvaluator extends Evaluator<Integer> {

  @Override
  protected ExportObject toExportObject(String identifier, Integer topEntries) {
    ExportParamsBuilder paramsBuilder = new ExportParamsBuilder();
    if (topEntries != null) {
      paramsBuilder.setTopKeys(topEntries);
    }
    return WordStatExport.create(identifier, results, paramsBuilder.build());
  }

}
