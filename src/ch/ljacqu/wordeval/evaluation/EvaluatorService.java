package ch.ljacqu.wordeval.evaluation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Service for evaluators, particularly for the handling of
 * {@link PostEvaluator} methods.
 */
public final class EvaluatorService {

  private EvaluatorService() {
  }

  /**
   * Returns a list of evaluators with a PostEvaluator method to be executed at
   * the end with the matched evaluator as value to execute the method with.
   * @param evaluators The list of given evaluators
   * @return Map of post evaluators (as key) and the matched evaluator to supply
   *         as parameter in the PostEvaluator method
   */
  @SuppressWarnings("rawtypes")
  public static Map<Evaluator<?>, Evaluator<?>> getPostEvaluators(Iterable<Evaluator<?>> evaluators) {
    Map<Evaluator, Class<? extends Evaluator>> postEvaluators = new HashMap<>();
    for (Evaluator evaluator : evaluators) {
      Method postEvalMethod = findPostEvaluatorMethod(evaluator);
      if (postEvalMethod != null) {
        @SuppressWarnings("unchecked")
        Class<? extends Evaluator> baseClass = (Class<? extends Evaluator>) postEvalMethod.getParameterTypes()[0];
        postEvaluators.put(evaluator, baseClass);
      }
    }
    return mapBaseClassToEvaluator(postEvaluators, evaluators);
  }

  /**
   * Executes the map of post evaluators.
   * @param postEvaluators The map of post evaluators where the key is the post
   *        evaluator (i.e. the evaluator with a @PostEvaluator method) and its
   *        Evaluator argument.
   */
  public static void executePostEvaluators(Map<Evaluator<?>, Evaluator<?>> postEvaluators) {
    for (Entry<Evaluator<?>, Evaluator<?>> evaluator : postEvaluators.entrySet()) {
      Method postEvalMethod = findPostEvaluatorMethod(evaluator.getKey());
      if (postEvalMethod == null) {
        throw new IllegalStateException("Evaluator '" + evaluator.getKey().getClass() + "' does not have a method"
            + " annotated with @PostEvaluator");
      }
      try {
        postEvalMethod.invoke(evaluator.getKey(), evaluator.getValue());
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new IllegalStateException("Could not invoke @PostEvaluator method", e);
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private static Map<Evaluator<?>, Evaluator<?>> mapBaseClassToEvaluator(
      Map<Evaluator, Class<? extends Evaluator>> postEvaluators, Iterable<Evaluator<?>> givenEvaluators) {
    Map<Evaluator<?>, Evaluator<?>> evaluators = new HashMap<>();
    
    for (Entry<Evaluator, Class<? extends Evaluator>> entry : postEvaluators.entrySet()) {
      boolean foundMatch = false;
      for (Evaluator evaluator : givenEvaluators) {
        if (evaluator.getClass().isAssignableFrom(entry.getValue())) {
          evaluators.put(entry.getKey(), evaluator);
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        throw new IllegalStateException("Could not match '" + entry.getValue() + "' for post evaluator '" 
            + entry.getKey().getClass() + "'");
      }
    }
    return evaluators;
  }

  /**
   * Returns the first found method of the class annotated with @PostEvaluator. 
   * Null if none available. Throws an exception if an @PostEvaluator method is found
   * and does not contain exactly one parameter that is a subtype (or equals) 
   * {@link Evaluator}.
   * @param evaluator The evaluator to process
   * @return The Method object annotated with @PostEvaluator or null if none found.
   */
  private static Method findPostEvaluatorMethod(Evaluator<?> evaluator) {
    for (Method method : evaluator.getClass().getMethods()) {
      if (Object.class.equals(method.getDeclaringClass())) {
        continue;
      }
      if (method.isAnnotationPresent(PostEvaluator.class)) {
        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length == 1 && Evaluator.class.isAssignableFrom(parameters[0])) {
          return method;
        }
        throw new IllegalStateException("Method '" + method.getName() + "' in '" + evaluator.getClass()
            + "' does not have exactly one parameter of (sub)type Evaluator");
      }
    }
    return null;
  }

}
