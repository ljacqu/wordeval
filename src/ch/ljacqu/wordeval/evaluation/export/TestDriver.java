package ch.ljacqu.wordeval.evaluation.export;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.LongWords;
import ch.ljacqu.wordeval.evaluation.SameLetterConsecutive;
import ch.ljacqu.wordeval.language.Dictionary;

public class TestDriver {

  public static void main(String[] args) throws Exception {
    List<Evaluator> evaluators = new ArrayList<Evaluator>();
    evaluators.add(new LongWords());
    evaluators.add(new SameLetterConsecutive());
    
    List<ExportObject> exportObjects = new ArrayList<ExportObject>(evaluators.size());

    Dictionary dictionary = Dictionary.getLanguageDictionary("hu", evaluators);
    dictionary.processDictionary();

    for (Evaluator evaluator : evaluators) {
      exportObjects.add(evaluator.toExportObject());
    }
    
    Gson gson = new Gson();
    System.out.println(gson.toJson(exportObjects));

  }

}
