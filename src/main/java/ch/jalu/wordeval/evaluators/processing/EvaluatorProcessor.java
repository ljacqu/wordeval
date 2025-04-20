package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.dictionary.Word;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Manages evaluators and triggers their evaluation process.
 */
@Component
public class EvaluatorProcessor {

  public void processAllWords(EvaluatorCollection evaluators, Collection<Word> words) {
    evaluators.allWordsEvaluators().forEach(evaluator -> evaluator.evaluate(words));
    evaluators.postEvaluators().forEach(evaluator -> evaluator.evaluate(evaluators));
  }
}
