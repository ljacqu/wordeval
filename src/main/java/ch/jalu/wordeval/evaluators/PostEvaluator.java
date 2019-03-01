package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;

/**
 * A post evaluator is an evaluator which produces results based on another evaluator's results.
 */
public interface PostEvaluator {

  void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore resultStore);

}
