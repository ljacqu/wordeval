package ch.ljacqu.wordeval.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Service for evaluators, particularly for the handling of
 * {@link PostEvaluator} instances.
 */
public final class EvaluatorService {

  private EvaluatorService() {
  }

  /**
   * Returns a list of evaluators with a PostEvaluator method to be executed at
   * the end with the matched evaluator as value to execute the method with.
   * @param evaluators the list of given evaluators
   * @return map of post evaluators (as key) and the matched evaluator to supply
   *         as parameter in the PostEvaluator method
   */
  public static Map<PostEvaluator<?>, Evaluator<?>> getPostEvaluators(Iterable<Evaluator<?>> evaluators) {
    List<PostEvaluator<?>> postEvaluators = new ArrayList<>();
    for (Evaluator<?> evaluator : evaluators) {
      if (evaluator instanceof PostEvaluator<?>) {
        PostEvaluator<?> postEvaluator = (PostEvaluator<?>) evaluator;
        postEvaluators.add(postEvaluator);
      }
    }
    return mapBaseClassToEvaluator(postEvaluators, evaluators);
  }

  /**
   * Executes the map of post evaluators.
   * @param postEvaluators the map of post evaluators where the key is the post
   *        evaluator (i.e. the evaluator with a @PostEvaluatorAnno method) and its
   *        Evaluator argument.
   */
  public static void executePostEvaluators(Map<PostEvaluator<?>, Evaluator<?>> postEvaluators) {
    for (Entry<PostEvaluator<?>, Evaluator<?>> evaluator : postEvaluators.entrySet()) {
      evaluator.getKey().castAndEvaluate(evaluator.getValue());
    }
  }
  
  private static Map<PostEvaluator<?>, Evaluator<?>> mapBaseClassToEvaluator(List<PostEvaluator<?>> conditions,
                                                                             Iterable<Evaluator<?>> givenEvaluators) {
    Map<PostEvaluator<?>, Evaluator<?>> evaluators = new HashMap<>();
    for (PostEvaluator<?> postEvaluator : conditions) {
      boolean foundMatch = false;
      for (Evaluator<?> potentialBase : givenEvaluators) {
        if (postEvaluator.isBaseMatch(potentialBase)) {
          evaluators.put(postEvaluator, potentialBase);
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        throw new IllegalStateException("Could not find match for '" + postEvaluator.getClass() + "'");
      }
    }
    return evaluators;
  }

}
