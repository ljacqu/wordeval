package ch.jalu.wordeval.runners;

import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.language.Language;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Service for initializing all available evaluators.
 */
@RequiredArgsConstructor
@Log4j2
public class EvaluatorInitializer {

  private static final String EVALUATOR_FOLDER = "src/main/java/ch/jalu/wordeval/evaluation";
  private static final String EVALUATOR_PACKAGE = "ch.jalu.wordeval.evaluation.";

  private final Language language;
  @Getter
  private final List<Evaluator<?>> evaluators = new ArrayList<>();

  /**
   * Builds a list of all available evaluators located in the {@link #EVALUATOR_FOLDER evaluators folder}.
   */
  public void buildAllEvaluators() {
    File[] files = new File(EVALUATOR_FOLDER).listFiles();
    if (files == null || files.length == 0) {
      throw new IllegalStateException("Could not read folder '" + EVALUATOR_FOLDER + "'");
    }
    for (File file : files) {
      if (file.isFile() && file.getName().endsWith(".java")) {
        instantiateEvaluator(getClassFromFile(file));
      }
    }
  }

  private void instantiateEvaluator(Class<? extends Evaluator<?>> clazz) {
    if (clazz != null) {
      Constructor<? extends Evaluator<?>> constructor =
          (Constructor<? extends Evaluator<?>>) clazz.getDeclaredConstructors()[0];
      // TODO: Should be on debug level
      log.info("Resolving instantation of class '{}'", clazz.getSimpleName());
      createEvaluators(constructor, new ArrayList<>(Arrays.asList(constructor.getParameterTypes())), new ArrayList<>());
    }
  }

  private Class<? extends Evaluator<?>> getClassFromFile(File file) {
    String className = EVALUATOR_PACKAGE + file.getName();
    try {
      // strip ".java"
      Class<?> clazz = Class.forName(className.substring(0, className.length() - 5));
      if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())
          && Evaluator.class.isAssignableFrom(clazz)) {
        return (Class<? extends Evaluator<?>>) clazz;
      }
    } catch (ClassNotFoundException e) {
      log.error("Could not load class '{}'", className);
    }
    return null;
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
    if (resolvedDependency.length == 1) {
      resolvedDependencies.add(resolvedDependency[0]);
      createEvaluators(constructor, dependencies, resolvedDependencies);
    } else {
      for (Object dep : resolvedDependency) {
        // Copy lists so each call has its own copy
        List<Class<?>> dependenciesCopy = new ArrayList<>(dependencies);
        List<Object> resolvedDependenciesCopy = new ArrayList<>(resolvedDependencies);
        resolvedDependenciesCopy.add(dep);
        createEvaluators(constructor, dependenciesCopy, resolvedDependenciesCopy);
      }
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
