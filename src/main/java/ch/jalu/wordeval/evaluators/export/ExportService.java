package ch.jalu.wordeval.evaluators.export;

import ch.jalu.wordeval.evaluators.Evaluator;
import ch.jalu.wordeval.language.Language;

import java.util.Comparator;
import java.util.stream.Stream;

public class ExportService {

  public void export(Language language, Stream<Evaluator> evaluators) {
    System.out.println(language.getName());
    evaluators
        .sorted(Comparator.comparing(Evaluator::getId))
        .forEach(evaluator -> {
          System.out.println(evaluator.getId() + " - " + evaluator.getTopResults(10, 20));
        });
    System.out.println();
  }
}
