package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.language.Language;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.stereotype.Component;

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
@Slf4j
@Component
public class EvaluatorInitializer {

  private static final String EVALUATOR_PACKAGE = "ch.jalu.wordeval.evaluators";

  private final Reflections reflections;

  /**
   * Constructor.
   */
  public EvaluatorInitializer() {
    this.reflections = new Reflections(EVALUATOR_PACKAGE, Scanners.SubTypes);
  }

  /**
   * Creates all possible evaluators from the evaluator package, {@link #EVALUATOR_PACKAGE}, for the given language.
   */
  public EvaluatorCollection createAllEvaluators(Language language) {
    List<AllWordsEvaluator> allWordsEvaluators = new ArrayList<>();
    List<PostEvaluator> postEvaluators = new ArrayList<>();
    ObjectCreator creator = new ObjectCreator(language);

    reflections.getSubTypesOf(AllWordsEvaluator.class).stream()
        .filter(this::isInstantiableClass)
        .forEach(clz -> creator.createObjectsAndSaveToList(clz, allWordsEvaluators));

    reflections.getSubTypesOf(PostEvaluator.class).stream()
        .filter(this::isInstantiableClass)
        .forEach(clz -> creator.createObjectsAndSaveToList(clz, postEvaluators));

    return new EvaluatorCollection(allWordsEvaluators, postEvaluators);
  }

  private boolean isInstantiableClass(Class<?> clazz) {
    return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
  }

  private static final class ObjectCreator {

    private final Language[] languageArray;
    private final Locale[] localeArray;

    ObjectCreator(Language language) {
      this.languageArray = new Language[]{ language };
      this.localeArray = new Locale[]{ language.getLocale() };
    }

    @SuppressWarnings("unchecked")
    <T> void createObjectsAndSaveToList(Class<T> clazz, List<? super T> list) {
      log.trace("Creating instances of class '{}'", clazz.getSimpleName());
      Constructor<T> constructor = (Constructor<T>) clazz.getDeclaredConstructors()[0];
      createAndAddObjects(list, constructor, newArrayList(constructor.getParameterTypes()), new ArrayList<>());
    }

    private <T> void createAndAddObjects(List<? super T> instancesList, Constructor<T> constructor,
                                         List<Class<?>> unresolvedDependencies, List<Object> resolvedDependencies) {
      if (unresolvedDependencies.isEmpty()) {
        instancesList.add(newInstance(constructor, resolvedDependencies));
      } else {
        Class<?> dependencyToResolve = unresolvedDependencies.removeFirst();
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
        return languageArray;
      } else if (clz.isEnum()) {
        return clz.getEnumConstants();
      } else if (Locale.class == clz) {
        return localeArray;
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
  }
}
