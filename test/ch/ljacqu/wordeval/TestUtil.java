package ch.ljacqu.wordeval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ch.ljacqu.wordeval.evaluation.Evaluator;

/**
 * Utility methods for the tests.
 */
public final class TestUtil {

  private TestUtil() {
  }

  /**
   * Helper methods to easily create a Set with the given items.
   * @param items the items to add into a set
   * @return a Set with the given items
   */
  public static Set<String> asSet(String... items) {
    return new HashSet<>(Arrays.asList(items));
  }

  /**
   * Creates a mutable list based on the input items.
   * @param item the items to create a list with
   * @return mutable list (as opposed to Arrays.asList()).
   */
  public static List<String> asList(String... item) {
    return new ArrayList<>(Arrays.asList(item));
  }

  public static void processWords(Evaluator<?> evaluator, String... words) {
    processWords(evaluator, words, words);
  }

  public static void processWords(Evaluator<?> evaluator, String[] cleanWords, String[] words) {
    for (int i = 0; i < cleanWords.length; ++i) {
      evaluator.processWord(cleanWords[i], words[i]);
    }
  }

  @SuppressWarnings("unchecked")
  public static Set<Object> toSet(Object o) {
    if (o instanceof Set<?>) {
      return (Set<Object>) o;
    }
    throw new IllegalArgumentException("Object '" + o + "' of type '" + o.getClass() + "' is not a Set");
  }

  @SuppressWarnings("unchecked")
  public static Collection<Object> toColl(Object o) {
    if (o instanceof Collection<?>) {
      return (Collection<Object>) o;
    }
    throw new IllegalArgumentException("Object '" + o + "' of type '" + o.getClass() + "' is not a Collection");
  }

}
