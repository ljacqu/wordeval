package ch.jalu.wordeval;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility methods for the tests.
 */
public final class TestUtil {

  private TestUtil() {
  }
  
  /**
   * Returns whether a dictionary's file exists or not.
   *
   * @param dictionary the dictionary to verify
   * @return true if the file exists, false otherwise
   */
  public static boolean doesDictionaryFileExist(Dictionary dictionary) {
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
    return Language.builder(code, "", alphabet);
  }
}
