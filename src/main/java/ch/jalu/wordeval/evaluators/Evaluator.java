package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.evaluators.result.EvaluationResult;

public sealed interface Evaluator<R extends EvaluationResult> permits AllWordsEvaluator, PostEvaluator {
}
