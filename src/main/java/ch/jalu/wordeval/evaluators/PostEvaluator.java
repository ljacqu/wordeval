package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.evaluators.processing.EvaluatorCollection;

/**
 * A post evaluator is an evaluator which produces results based on another evaluator's results.
 */
public non-sealed interface PostEvaluator extends Evaluator {

  void evaluate(EvaluatorCollection evaluators);

}
