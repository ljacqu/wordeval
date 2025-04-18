package ch.jalu.wordeval.evaluators.export;

import ch.jalu.wordeval.evaluators.Evaluator;
import ch.jalu.wordeval.language.Language;

import java.util.List;

public class ExportService {

  public void export(Language language, List<Evaluator> evaluators) {

    System.out.println(language);
    evaluators.forEach(evaluator -> {
      System.out.println(evaluator.getId() + " - " + evaluator.getTopResults(10, 20));
    });
    System.out.println();
  }
}
