package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.evaluators.processing.ResultStore;

import java.util.Collection;

/**
 * A post evaluator is an evaluator which produces results based on another evaluator's results.
 */
// Note 20170504: For now we bind B (base evaluator type) to WordEvaluator and not generally to Evaluator;
// if the need arises it should be possible to generalize this.
public interface PostEvaluator<B extends WordEvaluator<BR>,
                               BR extends Comparable<BR>,
                               R extends Comparable<R>> extends Evaluator {

  Class<B> getBaseClass();

  Collection<EvaluatedWord<R>> evaluate(B baseEvaluator, ResultStore<BR> baseResults);

  default boolean isBaseMatch(B baseEvaluator) {
    return true;
  }
}
