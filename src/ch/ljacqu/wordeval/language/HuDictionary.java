package ch.ljacqu.wordeval.language;

import java.util.List;

import ch.ljacqu.wordeval.evaluation.Evaluator;

public class HuDictionary extends Dictionary {

  public static final String CODE = "hu";

  public HuDictionary(List<Evaluator> evaluators) {
    this("dict/hu.dic", evaluators);
  }

  public HuDictionary(String fileName, List<Evaluator> evaluators) {
    super(fileName, CODE, evaluators, '/', '\t');
  }

}
