package ch.ljacqu.wordeval.language;

import java.util.List;

import ch.ljacqu.wordeval.evaluation.Evaluator;

public class TrDictionary extends Dictionary {

  public static final String CODE = "tr";

  public TrDictionary(List<Evaluator> evaluators) {
    this("dict/tr.dic", evaluators);
  }

  public TrDictionary(String fileName, List<Evaluator> evaluators) {
    super(fileName, CODE, evaluators, ' ');
  }

}
