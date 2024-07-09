package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.language.Language;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Service for initializing all available evaluators.
 */
@Log4j2
public class EvaluatorInitializer {

  private static final String EVALUATOR_PACKAGE = "ch.jalu.wordeval.evaluators";

  private final Language[] language;
  private final Locale[] locale;

  @Getter
  private final List<AllWordsEvaluator<?>> allWordsEvaluators = new ArrayList<>();
  @Getter
  private final List<PostEvaluator<?>> postEvaluators = new ArrayList<>();

  /**
   * Constructor.
   *
   * @param language the language to construct evaluators for
   */
  public EvaluatorInitializer(Language language) {
    this.language = new Language[]{ language };
    this.locale = new Locale[]{ language.getLocale() };
    createAllEvaluators();
  }

  public int getEvaluatorsCount() {
    return allWordsEvaluators.size() + postEvaluators.size();
  }

  /**
   * Creates all possible evaluators from the evaluator package, {@link #EVALUATOR_PACKAGE}.
   */
  private void createAllEvaluators() {
    Reflections reflections = new Reflections(EVALUATOR_PACKAGE, new SubTypesScanner(false));

    reflections.getSubTypesOf(AllWordsEvaluator.class).stream()
      .filter(this::isInstantiableClass)
      .forEach(clz -> createObjectsAndSaveToList(clz, (List) allWordsEvaluators));

    reflections.getSubTypesOf(PostEvaluator.class).stream()
      .filter(this::isInstantiableClass)
      .forEach(clz -> createObjectsAndSaveToList(clz, (List) postEvaluators));
  }

  private <T> void createObjectsAndSaveToList(Class<T> clazz, List<? super T> list) {
    log.trace("Creating instances of class '{}'", clazz.getSimpleName());
    Constructor<T> constructor = (Constructor<T>) clazz.getDeclaredConstructors()[0];
    createAndAddObjects(list, constructor, newArrayList(constructor.getParameterTypes()), new ArrayList<>());
  }

  private <T> void createAndAddObjects(List<? super T> instancesList, Constructor<T> constructor,
                                       List<Class<?>> unresolvedDependencies, List<Object> resolvedDependencies) {
    if (unresolvedDependencies.isEmpty()) {
      instancesList.add(newInstance(constructor, resolvedDependencies));
    } else {
      Class<?> dependencyToResolve = unresolvedDependencies.remove(0);
      Object[] resolvedDependencyValues = resolveDependency(dependencyToResolve);
      for (Object curDependency : resolvedDependencyValues) {
        // Copy lists so each call has its own copy
        List<Class<?>> curUnresolvedDependencies = new ArrayList<>(unresolvedDependencies);
        List<Object> curResolvedDependencies = new ArrayList<>(resolvedDependencies);
        curResolvedDependencies.add(curDependency);
        createAndAddObjects(instancesList, constructor, curUnresolvedDependencies, curResolvedDependencies);
      }
    }
  }

  private Object[] resolveDependency(Class<?> clz) {
    if (Language.class == clz) {
      return language;
    } else if (clz.isEnum()) {
      return clz.getEnumConstants();
    } else if (Locale.class == clz) {
      return locale;
    }
    throw new IllegalStateException("Unknown what value to provide for '" + clz + "'");
  }

  private static <T> T newInstance(Constructor<T> constructor, List<Object> arguments) {
    try {
      return constructor.newInstance(arguments.toArray());
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  private boolean isInstantiableClass(Class<?> clazz) {
    return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
  }
}
