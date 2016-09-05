package ch.jalu.wordeval;

import ch.jalu.wordeval.dictionary.DictionarySettings;
import ch.jalu.wordeval.evaluation.DictionaryEvaluator;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;

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
  public static void processWords(DictionaryEvaluator<?> evaluator, String... words) {
    processWords(evaluator, words, words);
  }

  /**
   * Makes an evaluator process the given list of words.
   * @param evaluator the evaluator to process the words with
   * @param cleanWords the list of words in a certain WordForm format
   * @param words the list of words in their RAW WordFormat
   */
  public static void processWords(DictionaryEvaluator<?> evaluator, String[] cleanWords, String[] words) {
    for (int i = 0; i < cleanWords.length; ++i) {
      evaluator.processWord(cleanWords[i], words[i]);
    }
  }
  
  /**
   * Returns whether a dictionary's file exists or not.
   * @param dictionary the dictionary to verify
   * @return true if the file exists, false otherwise
   */
  public static boolean doesDictionaryFileExist(DictionarySettings dictionary) {
    return Files.exists(Paths.get(dictionary.getFile()));
  }

  /**
   * Initializes a new Language builder with the given code and the Latin alphabet.
   *
   * @param code the language code
   * @return the Language builder
   */
  public static Language.Builder newLanguage(String code) {
    return newLanguage(code, Alphabet.LATIN);
  }

  /**
   * Initializes a new Language builder with the given code and alphabet.
   *
   * @param code the language code
   * @param alphabet the alphabet
   * @return the generated Language builder
   */
  public static Language.Builder newLanguage(String code, Alphabet alphabet) {
    return Language.builder()
        .code(code)
        .alphabet(alphabet)
        .name("");
  }

}
