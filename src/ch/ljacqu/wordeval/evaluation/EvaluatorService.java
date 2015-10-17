package ch.ljacqu.wordeval.evaluation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

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
    Map<Evaluator, Pair<Class<? extends Evaluator>, Method>> postEvaluators = new HashMap<>();
    for (Evaluator evaluator : evaluators) {
      Method postEvalMethod = findMethodWithAnnotation(evaluator, PostEvaluator.class);
      Method baseMatcherMethod = findMethodWithAnnotation(evaluator, BaseMatcher.class);
      if (postEvalMethod == null && baseMatcherMethod != null) {
        throw new IllegalStateException("Found @BaseMatcher method without @PostEvaluator method in '"
            + evaluator.getClass() + "'");
      } else if (postEvalMethod != null) {
        if (baseMatcherMethod != null) {
          if (!postEvalMethod.getParameterTypes()[0]
              .equals(baseMatcherMethod.getParameterTypes()[0])) {
            throw new IllegalStateException("Parameter in @BaseMatcher does not match the one in @PostEvaluator");
          }
        }
        @SuppressWarnings("unchecked")
        Class<? extends Evaluator> baseClass = (Class<? extends Evaluator>) postEvalMethod.getParameterTypes()[0];
        postEvaluators.put(evaluator, Pair.of(baseClass, baseMatcherMethod));
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
      Method postEvalMethod = findMethodWithAnnotation(evaluator.getKey(), PostEvaluator.class);
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
      Map<Evaluator, Pair<Class<? extends Evaluator>, Method>> postEvaluators, 
      Iterable<Evaluator<?>> givenEvaluators) {
    System.out.println(postEvaluators);
    Map<Evaluator<?>, Evaluator<?>> evaluators = new HashMap<>();
    
    for (Entry<Evaluator, Pair<Class<? extends Evaluator>, Method>> entry : postEvaluators.entrySet()) {
      boolean foundMatch = false;
      for (Evaluator evaluator : givenEvaluators) {
        if (evaluator.getClass().isAssignableFrom(entry.getValue().getLeft())) {
          Method matcher = entry.getValue().getRight();
          try {
            if (matcher == null || Boolean.TRUE.equals(
                matcher.invoke(entry.getKey(), evaluator))) {
              evaluators.put(entry.getKey(), evaluator);
              foundMatch = true;
              break;
            }
          } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException("Could not invoke BaseMatcher for '"
                + entry.getKey().getClass() + "'", e);
          }
        }
      }
      if (!foundMatch) {
        throw new IllegalStateException("Could not match '" + entry.getValue() + "' for post evaluator '" 
            + entry.getKey().getClass() + "'");
      }
    }
    return evaluators;
  }
  
  private static Method findMethodWithAnnotation(Evaluator<?> evaluator, 
      Class<? extends Annotation> annotation) {
    Optional<Method> method = Arrays.stream(evaluator.getClass().getMethods())
      .filter(m -> m.isAnnotationPresent(annotation))
      .findFirst();
    if (!method.isPresent()) {
      return null;
    }
    Class<?>[] parameters = method.get().getParameterTypes();
    if (parameters.length == 1 && Evaluator.class.isAssignableFrom(parameters[0])) {
      return method.get();
    } else {
      throw new IllegalStateException("Method with @" + annotation.getClass().getSimpleName()
          + " must have one parameter with (sub)type Evaluator");
    }
  }
  
  private static Object getDeclaredFieldValue(Field field, Object object) {
    field.setAccessible(true);
    try {
      return field.get(object);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

}
