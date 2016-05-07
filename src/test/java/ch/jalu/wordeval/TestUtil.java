package ch.jalu.wordeval;

import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility methods for the tests.
 */
public final class TestUtil {

  private TestUtil() {
  }

  /**
   * Helper method to easily create a mutable Set with the given items.
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
   * Returns whether a dictionary's file exists or not.
   * @param dictionary the dictionary to verify
   * @return true if the file exists, false otherwise
   */
  public static boolean doesDictionaryFileExist(Dictionary dictionary) {
    return Files.exists(Paths.get(dictionary.getFileName()));
  }

  /**
   * Initializes a new Language object with the given code and the Latin alphabet.
   *
   * @param code the language code
   * @return the generated Language instance
   */
  public static Language newLanguage(String code) {
    return newLanguage(code, Alphabet.LATIN);
  }

  /**
   * Initializes a new Language object with the given code and alphabet.
   *
   * @param code the language code
   * @param alphabet the alphabet
   * @return the generated Language instance
   */
  public static Language newLanguage(String code, Alphabet alphabet) {
    return new Language(code, "", alphabet);
  }

  /**
   * Class for Reflection utils in tests.
   */
  public static final class R {
    private R() {
    }

    /**
     * Retrieves the fields of the given instance by reflection.
     * @param clz the class of the instance
     * @param instance the instance to retrieve the field from, or null for static fields
     * @param fieldName the name of the field
     * @param <T> the type of the instance
     * @return the value of the given field
     */
    public static <T> Object getField(Class<T> clz, T instance, String fieldName) {
      Field field;
      try {
        field = clz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
      } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
        throw new IllegalStateException("Could not get field '" + fieldName + "' from '" + clz + "'", e);
      }
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

    /**
     * Retrieves the method from a class with the given name and parameters.
     * @param clz the class to retrieve the method from
     * @param methodName the name of the method
     * @param params the parameter types the method takes
     * @param <T> the type of the class
     * @return the retrieved Method object
     */
    public static <T> Method getMethod(Class<T> clz, String methodName, Class<?>... params) {
      try {
        Method m = clz.getDeclaredMethod(methodName, params);
        m.setAccessible(true);
        return m;
      } catch (NoSuchMethodException | SecurityException e) {
        throw new IllegalStateException(
            "Could not get method '" + methodName + "' from class '" + clz + "'", e);
      }
    }

    /**
     * Invokes a method on the given instance with the provided parameters.
     * @param method the method to invoke
     * @param instance the instance to invoke the method on
     * @param params the parameters to invoke the method with
     * @return the return value of the method
     */
    public static Object invokeMethod(Method method, Object instance, Object... params) {
      try {
        return method.invoke(instance, params);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new IllegalStateException("Could not invoke '" + method + "'", e);
      }
    }
  }

  /**
   * Helper class to initialize a list with when a more verbose structure than {@link Arrays#asList} is desired.
   * @param <T> the generic type of the list
   */
  public static class ANewList<T> {
    @Getter
    private List<T> list;

    private ANewList() {
      list = new ArrayList<>();
    }

    /**
     * Generates a list builder with the first entry to add.
     * @param value the value to add to the list
     * @return the list builder
     */
    public static <T> ANewList with(T value) {
      ANewList<T> listInit = new ANewList<>();
      listInit.and(value);
      return listInit;
    }

    /**
     * Adds a value to the list that is being built.
     * @param value the value to add
     * @return the list builder
     */
    public ANewList and(T value) {
      list.add(value);
      return this;
    }
  }
}
