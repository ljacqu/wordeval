package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.evaluators.result.EvaluationResult;
import com.google.common.collect.ListMultimap;

import java.util.Collections;
import java.util.List;

public sealed interface Evaluator<R extends EvaluationResult> permits AllWordsEvaluator, PostEvaluator {

  default List<ListMultimap<Object, Object>> getTopResults(int topScores, int maxLimit) {
    return Collections.emptyList();
  }

  default String getId() {
    return getClass().getSimpleName();
  }
}
