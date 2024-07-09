package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.evaluators.result.EvaluationResult;

/**
 * A post evaluator is an evaluator which produces results based on another evaluator's results.
 */
public non-sealed interface PostEvaluator<R extends EvaluationResult> extends Evaluator<R> {

  void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore<R> resultStore);

}
