package ch.ljacqu.wordeval.dictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import ch.ljacqu.wordeval.language.Language;

/**
 * Class containing dictionary-specific parameters, based on which a sanitizer
 * can be generated for the dictionary.
 */
@Getter
class DictionarySettings {

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

  // List of known dictionary settings
  static {
    add("af").setDelimiters('/').setSkipSequences(".", "µ", "Ð", "ø");
    add("en-us").setDelimiters('/');
    // TODO Basque: Some entries have _ but most parts seem to be present alone
    add("eu").setDelimiters('/').setSkipSequences(".", "+", "_");
    add("fr", FrSanitizer.class);
    add("hu", HuSanitizer.class);
    add("ru").setDelimiters('/').setSkipSequences(".");
    add("tr").setDelimiters(' ');
  }

  DictionarySettings(String identifier) {
    this.identifier = identifier;
  }
  
  private static DictionarySettings add(String code) {
    DictionarySettings dictionarySettings = new DictionarySettings(code);
    settings.put(code, dictionarySettings);
    return dictionarySettings;
  }
  
  private static void add(String code, Class<? extends Sanitizer> sanitizerClass) {
    DictionarySettings dictionarySettings = new CustomSettings(code, sanitizerClass);
    settings.put(code, dictionarySettings);
  }

  static DictionarySettings get(String identifier) {
    DictionarySettings result = settings.get(identifier);
    if (result == null) {
      throw new IllegalArgumentException(
          "Dictionary settings with identifier '" + identifier + "' is unknown");
    }
    return result;
  }
  
  static Set<String> getAllCodes() {
    return settings.keySet();
  }

  // --- Build sanitizer
  Sanitizer buildSanitizer(Language language) {
    return new Sanitizer(language, this);
  }

  // --- Delimiters
  DictionarySettings setDelimiters(char... delimiters) {
    this.delimiters = delimiters;
    return this;
  }

  // --- Skip sequences
  DictionarySettings setSkipSequences(String... skipSequences) {
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
    Sanitizer buildSanitizer(Language language) {
      try {
        return sanitizerClass.newInstance();
      } catch (IllegalAccessException | InstantiationException e) {
        throw new UnsupportedOperationException("Could not get sanitizer", e);
      }
    }
  }

}
