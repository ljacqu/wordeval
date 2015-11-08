package ch.ljacqu.wordeval;

import java.lang.reflect.Field;
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

  /**
   * Makes an evaluator process the given list of words.
   * @param evaluator the evaluator to process the words with
   * @param words the words to process
   */
  public static void processWords(Evaluator<?> evaluator, String... words) {
    processWords(evaluator, words, words);
  }

  /**
   * Makes an evaluator process the given list of words.
   * @param evaluator the evaluator to process the words with
   * @param cleanWords the list of words in a certain WordForm format
   * @param words the list of words in their RAW WordFormat
   */
  public static void processWords(Evaluator<?> evaluator, String[] cleanWords, String[] words) {
    for (int i = 0; i < cleanWords.length; ++i) {
      evaluator.processWord(cleanWords[i], words[i]);
    }
  }

  /**
   * Verifies that an object is an instance of {@link Set} and returns the casted value
   * or throws an exception otherwise.
   * @param o the object to verify
   * @return the casted Set
   */
  @SuppressWarnings("unchecked")
  public static Set<Object> toSet(Object o) {
    if (o instanceof Set<?>) {
      return (Set<Object>) o;
    }
    throw new IllegalArgumentException("Object '" + o + "' of type '" + o.getClass() + "' is not a Set");
  }

  /**
   * Verifies that an object is an instance of {@link Collection} and returns the casted value
   * or throws an exception otherwise.
   * @param o the object to verify
   * @return the casted Collection
   */
  @SuppressWarnings("unchecked")
  public static Collection<Object> toColl(Object o) {
    if (o instanceof Collection<?>) {
      return (Collection<Object>) o;
    }
    throw new IllegalArgumentException("Object '" + o + "' of type '" + o.getClass() + "' is not a Collection");
  }
  
  /**
   * Sets the field value of an object or class via reflection.
   * @param <T> the type of the class
   * @param clz the class
   * @param instance the instance to set the value for, or null for static fields
   * @param fieldName the name of the field to set
   * @param value the value to set to the field
   */
  public static <T> void setField(Class<T> clz, T instance, String fieldName, Object value) {
    try {
      Field field = clz.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(instance, value);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
      throw new UnsupportedOperationException("Could not set field", e);
    }
  }

}
