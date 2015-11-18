package ch.ljacqu.wordeval.dictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

/**
 * Class containing dictionary-specific parameters, based on which a sanitizer
 * can be generated for the dictionary.
 */
@Getter
public class DictionarySettings {

  private static Map<String, DictionarySettings> settings = new HashMap<>();

  /**
   * The dictionary identifier is typically the ISO-639-1 abbreviation of its
   * language.
   */
  private final String identifier;
  /**
   * Collection of characters to search for in a read line. The line is cut
   * before the first occurrence of a delimiter, returning the word without any
   * additional data the dictionary may store.
   */
  private char[] delimiters;
  /**
   * Collection of skip sequences - if any such sequence is found in the word,
   * it is skipped.
   */
  private String[] skipSequences;

  DictionarySettings(String identifier) {
    this.identifier = identifier;
  }
  
  /**
   * Creates and saves a new set of dictionary settings for the given identifier.
   * @param identifier The dictionary identifier to create settings for
   * @return The created DictionarySettings object
   */
  public static DictionarySettings add(String identifier) {
    DictionarySettings dictionarySettings = new DictionarySettings(identifier);
    settings.put(dictionarySettings.identifier, dictionarySettings);
    return dictionarySettings;
  }
  
  /**
   * Saves a new dictionary settings entry with a custom sanitizer.
   * @param identifier The identifier of the dictionary
   * @param sanitizerClass The class of the custom sanitizer. It must have a
   * public no-arguments constructor.
   */
  public static void add(String identifier, Class<? extends Sanitizer> sanitizerClass) {
    DictionarySettings dictionarySettings = new CustomSettings(identifier, sanitizerClass);
    settings.put(identifier, dictionarySettings);
  }

  /**
   * Gets the dictionary settings for the given identifier.
   * @param identifier The identifier to retrieve the settings for
   * @return The dictionary settings
   */
  public static DictionarySettings get(String identifier) {
    DictionarySettings result = settings.get(identifier);
    if (result == null) {
      throw new IllegalArgumentException("Dictionary settings with identifier '" + identifier + "' is unknown");
    }
    return result;
  }

  /**
   * Returns all known dictionary settings.
   * @return List of all dictionary codes
   */
  public static Set<String> getAllCodes() {
    return settings.keySet();
  }

  /**
   * Builds a sanitizer with the required information.
   * @return The created sanitizer
   */
  Sanitizer buildSanitizer() {
    return new Sanitizer(this);
  }

  /**
   * Sets the delimiters for the dictionary.
   * @param delimiters The delimiters to set
   * @return The current DictionarySettings object
   */
  public DictionarySettings setDelimiters(char... delimiters) {
    this.delimiters = delimiters;
    return this;
  }

  /**
   * Sets the skip sequences for the dictionary.
   * @param skipSequences The skip sequences to set
   * @return The current DictionarySettings object
   */
  public DictionarySettings setSkipSequences(String... skipSequences) {
    this.skipSequences = skipSequences;
    return this;
  }

  /**
   * Subtype of dictionary settings for dictionaries that have a custom
   * sanitizer. The custom sanitizer must have a no-args constructor.
   */
  static class CustomSettings extends DictionarySettings {
    private final Class<? extends Sanitizer> sanitizerClass;

    CustomSettings(String identifier, Class<? extends Sanitizer> sanitizerClass) {
      super(identifier);
      this.sanitizerClass = sanitizerClass;
    }

    @Override
    Sanitizer buildSanitizer() {
      try {
        return sanitizerClass.newInstance();
      } catch (IllegalAccessException | InstantiationException e) {
        throw new UnsupportedOperationException("Could not create sanitizer", e);
      }
    }
  }

}
