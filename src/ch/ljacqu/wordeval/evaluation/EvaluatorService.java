package ch.ljacqu.wordeval.evaluation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import lombok.AllArgsConstructor;

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
   * @param evaluators the list of given evaluators
   * @return map of post evaluators (as key) and the matched evaluator to supply
   *         as parameter in the PostEvaluator method
   */
  public static Map<Evaluator<?>, Evaluator<?>> getPostEvaluators(Iterable<Evaluator<?>> evaluators) {
    List<PostEvaluatorCondition> conditions = new ArrayList<>();
    for (Evaluator<?> evaluator : evaluators) {
      Method postEvalMethod = findMethodWithAnnotation(evaluator, PostEvaluator.class);
      Method baseMatcherMethod = findMethodWithAnnotation(evaluator, BaseMatcher.class);
      if (postEvalMethod == null && baseMatcherMethod != null) {
        throw new IllegalStateException("Found @BaseMatcher method without @PostEvaluator method in '"
            + evaluator.getClass() + "'");
      } else if (postEvalMethod != null) {
        validateBaseMatcher(baseMatcherMethod, postEvalMethod);
        @SuppressWarnings("unchecked")
        Class<? extends Evaluator<?>> baseClass = 
            (Class<? extends Evaluator<?>>) postEvalMethod.getParameterTypes()[0];
        conditions.add(new PostEvaluatorCondition(evaluator, baseClass, baseMatcherMethod));
      }
    }
    return mapBaseClassToEvaluator(conditions, evaluators);
  }

  /**
   * Executes the map of post evaluators.
   * @param postEvaluators the map of post evaluators where the key is the post
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
  
  private static Map<Evaluator<?>, Evaluator<?>> mapBaseClassToEvaluator(
      List<PostEvaluatorCondition> conditions,
      Iterable<Evaluator<?>> givenEvaluators) {
    Map<Evaluator<?>, Evaluator<?>> evaluators = new HashMap<>();
    for (PostEvaluatorCondition condition : conditions) {
      boolean foundMatch = false;
      for (Evaluator<?> potentialBase : givenEvaluators) {
        if (condition.baseClass.isAssignableFrom(potentialBase.getClass())
            && isBaseMatch(condition, potentialBase)) {
          evaluators.put(condition.postEvaluator, potentialBase);
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        throw new IllegalStateException("Could not find match for '"
            + condition.postEvaluator.getClass() + "'");
      }
    }
    return evaluators;
  }
  
  private static void validateBaseMatcher(Method baseMatcherMethod, Method postEvalMethod) {
    if (baseMatcherMethod != null) {
      if (!postEvalMethod.getParameterTypes()[0]
          .equals(baseMatcherMethod.getParameterTypes()[0])) {
        throw new IllegalStateException("Parameter in @BaseMatcher does not match the one in @PostEvaluator");
      } else if (!boolean.class.equals(baseMatcherMethod.getReturnType())
          && !Boolean.class.equals(baseMatcherMethod.getReturnType())) {
        throw new IllegalStateException("Method @BaseMatcher must return a boolean");
      }
    }
  }
  
  private static boolean isBaseMatch(PostEvaluatorCondition condition, Evaluator<?> potentialBase) {
    if (condition.baseMatcher == null) {
      return true;
    }
    try {
      return Boolean.TRUE.equals(
          condition.baseMatcher.invoke(condition.postEvaluator, potentialBase));
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new IllegalStateException("Could not invoke BaseMatcher function on '" 
        + condition.postEvaluator.getClass() + "' with '" + potentialBase.getClass() + "'", e);
    }
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
      throw new IllegalStateException("Method with @" + annotation.getSimpleName()
          + " must have one parameter of (sub)type Evaluator");
    }
  }
  
  @AllArgsConstructor
  private static final class PostEvaluatorCondition {
    public final Evaluator<?> postEvaluator;
    public final Class<? extends Evaluator<?>> baseClass;
    public final Method baseMatcher;
  }

}
