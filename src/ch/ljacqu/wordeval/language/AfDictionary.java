package ch.ljacqu.wordeval.language;

import java.util.List;

import ch.ljacqu.wordeval.evaluation.Evaluator;

public class AfDictionary extends Dictionary {

  public static final String CODE = "af";

  public AfDictionary(List<Evaluator> evaluators) {
    this("dict/af.dic", evaluators);
  }

  public AfDictionary(String fileName, List<Evaluator> evaluators) {
    super(fileName, CODE, evaluators, '/');
  }

}
