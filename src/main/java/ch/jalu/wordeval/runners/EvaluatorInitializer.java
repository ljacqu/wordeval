package ch.jalu.wordeval.runners;

import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.language.Language;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for initializing all available evaluators.
 */
@Log4j2
public class EvaluatorInitializer {

  private static final String EVALUATOR_PACKAGE = "ch.jalu.wordeval.evaluation";

  private final Language language;
  @Getter
  private final List<Evaluator<?>> evaluators = new ArrayList<>();

  /**
   * Constructor.
   *
   * @param language the language to construct evaluators for
   */
  public EvaluatorInitializer(Language language) {
    this.language = language;
    buildAllEvaluators();
  }

  /**
   * Creates all possible evaluators from the evaluator package, {@link #EVALUATOR_PACKAGE}.
   */
  private void buildAllEvaluators() {
    Reflections reflections = new Reflections(EVALUATOR_PACKAGE, new SubTypesScanner());

    Set<Class<? extends Evaluator>> evaluatorClasses = reflections.getSubTypesOf(Evaluator.class).stream()
        .filter(clz -> !clz.isInterface() && !Modifier.isAbstract(clz.getModifiers()))
        .filter(clz -> !clz.isMemberClass() || !clz.getDeclaringClass().getName().endsWith("Test"))
        .collect(Collectors.toSet());

    evaluatorClasses.forEach(this::instantiateEvaluator);
  }

  private void instantiateEvaluator(Class<? extends Evaluator> clazz) {
    if (clazz != null) {
      Constructor<? extends Evaluator<?>> constructor =
          (Constructor<? extends Evaluator<?>>) clazz.getDeclaredConstructors()[0];
      log.debug("Resolving instantiation of class '{}'", clazz.getSimpleName());
      createEvaluators(constructor, new ArrayList<>(Arrays.asList(constructor.getParameterTypes())), new ArrayList<>());
    }
  }

  private void createEvaluators(Constructor<? extends Evaluator<?>> constructor,
                                List<Class<?>> dependencies,
                                List<Object> resolvedDependencies) {
    if (dependencies.isEmpty()) {
      evaluators.add(newInstance(constructor, resolvedDependencies));
      return;
    }

    Class<?> dependency = dependencies.remove(0);
    Object[] resolvedDependency = resolveDependency(dependency);
    for (Object dep : resolvedDependency) {
      // Copy lists so each call has its own copy
      List<Class<?>> dependenciesCopy = new ArrayList<>(dependencies);
      List<Object> resolvedDependenciesCopy = new ArrayList<>(resolvedDependencies);
      resolvedDependenciesCopy.add(dep);
      createEvaluators(constructor, dependenciesCopy, resolvedDependenciesCopy);
    }
  }

  private Object[] resolveDependency(Class<?> clz) {
    if (Language.class == clz) {
      return new Object[]{language};
    } else if (clz.isEnum()) {
      return clz.getEnumConstants();
    } else if (Locale.class == clz) {
      return new Object[]{language.getLocale()};
    }
    throw new IllegalStateException("Unknown what value to provide for '" + clz + "'");
  }

  private static Evaluator<?> newInstance(Constructor<? extends Evaluator> constructor, List<Object> arguments) {
    try {
      return constructor.newInstance(arguments.toArray());
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }
}
