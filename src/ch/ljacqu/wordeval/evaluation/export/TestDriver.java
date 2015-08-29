package ch.ljacqu.wordeval.evaluation.export;

import java.util.ArrayList;
import java.util.List;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.LongWords;
import ch.ljacqu.wordeval.evaluation.SameLetterConsecutive;
import ch.ljacqu.wordeval.language.Dictionary;

public class TestDriver {

  public static void main(String[] args) throws Exception {
    List<Evaluator> evaluators = new ArrayList<Evaluator>();
    //evaluators.add(new LongWords());
    evaluators.add(new SameLetterConsecutive());

    Dictionary dictionary = Dictionary.getLanguageDictionary("af", evaluators);
    dictionary.processDictionary();

    ResultsExporter exporter = new ResultsExporter();

    for (Evaluator evaluator : evaluators) {
      System.out.println(exporter.toJson(evaluator.toExportObject()));
      //System.out.println(evaluator.toExportObject());
    }

  }

}
