package ch.ljacqu.wordeval.dictionary;

import java.util.HashMap;
import java.util.Map;
import ch.ljacqu.wordeval.language.Language;

/**
 * Class containing dictionary-specific parameters, based on which a sanitizer
 * can be generated for the dictionary.
 */
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
    add("af").setDelimiters('/').setSkipSequences(".",
        "µ", "Ð", "ø");
    add("en-us").setDelimiters('/');
    add(new CustomSettings("hu", HuSanitizer.class));
    add("tr").setDelimiters(' ');
  }

  DictionarySettings(String identifier) {
    this.identifier = identifier;
  }

  private static void add(DictionarySettings dictionarySettings) {
    settings.put(dictionarySettings.identifier, dictionarySettings);
  }
  
  private static DictionarySettings add(String code) {
    DictionarySettings dictionarySettings = new DictionarySettings(code);
    settings.put(code, dictionarySettings);
    return dictionarySettings;
  }

  public static DictionarySettings get(String identifier) {
    DictionarySettings result = settings.get(identifier);
    if (result == null) {
      throw new IllegalArgumentException(
          "Dictionary settings with identifier '" + identifier + "' is unknown");
    }
    return result;
  }

  // --- Build sanitizer
  public Sanitizer buildSanitizer(Language language) {
    return new Sanitizer(language, this);
  }

  // --- Delimiters
  DictionarySettings setDelimiters(char... delimiters) {
    this.delimiters = delimiters;
    return this;
  }

  public char[] getDelimiters() {
    return delimiters;
  }

  // --- Skip sequences
  DictionarySettings setSkipSequences(String... skipSequences) {
    this.skipSequences = skipSequences;
    return this;
  }

  public String[] getSkipSequences() {
    return skipSequences;
  }

  /**
   * Subtype of dictionary settings for dictionaries that have a custom
   * sanitizer. The custom sanitizer must have a no-args constructor.
   */
  public static class CustomSettings extends DictionarySettings {
    private final Class<? extends Sanitizer> sanitizerClass;

    public CustomSettings(String identifier,
        Class<? extends Sanitizer> sanitizerClass) {
      super(identifier);
      this.sanitizerClass = sanitizerClass;
    }

    @Override
    public Sanitizer buildSanitizer(Language language) {
      try {
        return sanitizerClass.newInstance();
      } catch (IllegalAccessException | InstantiationException e) {
        throw new UnsupportedOperationException("Could not get sanitizer", e);
      }
    }
  }

}
