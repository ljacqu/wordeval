package ch.jalu.wordeval.evaluators;

import com.google.common.collect.ListMultimap;

public sealed interface Evaluator permits AllWordsEvaluator, PostEvaluator {

  ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit);

  default String getId() {
    return getClass().getSimpleName();
  }
}
