package ch.jalu.wordeval.evaluators;

import com.google.common.collect.ListMultimap;
import org.apache.commons.lang3.StringUtils;

public sealed interface Evaluator permits AllWordsEvaluator, PostEvaluator {

  ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit);

  default String getId() {
    return StringUtils.uncapitalize(getClass().getSimpleName());
  }
}
