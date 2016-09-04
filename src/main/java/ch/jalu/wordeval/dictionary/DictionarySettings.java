package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.language.Language;
import lombok.Getter;

import java.util.Objects;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * Class containing dictionary-specific parameters, based on which a sanitizer
 * can be generated for the dictionary.
 */
@Getter
public class DictionarySettings {

  /**
   * The dictionary identifier is typically the ISO-639-1 abbreviation of its
   * language.
   */
  private final String identifier;

  private final String file;

  private final Language language;

  /**
   * Collection of characters to search for in a read line. The line is cut
   * before the first occurrence of a delimiter, returning the word without any
   * additional data the dictionary may store.
   */
  private final char[] delimiters;
  /**
   * Collection of skip sequences - if any such sequence is found in the word,
   * it is skipped.
   */
  private final String[] skipSequences;

  private final Class<? extends Sanitizer> sanitizerClass;

  @Getter(lazy = true)
  private final Sanitizer sanitizer = buildSanitizer();

  private DictionarySettings(String identifier, String file, Language language, char[] delimiters,
                             String[] skipSequences, Class<? extends Sanitizer> sanitizerClass) {
    this.identifier = identifier;
    this.file = file;
    this.language = language;
    this.delimiters = delimiters;
    this.skipSequences = skipSequences;
    this.sanitizerClass = sanitizerClass;
  }

  /**
   * Builds a sanitizer with the required information.
   * @return The created sanitizer
   */
  Sanitizer buildSanitizer() {
    return sanitizerClass == null
        ? new Sanitizer(this)
        : createSanitizer(sanitizerClass);
  }

  private static <T extends Sanitizer> T createSanitizer(Class<T> clazz) {
    try {
      return clazz.newInstance();
    } catch (IllegalAccessException | InstantiationException e) {
      throw new UnsupportedOperationException("Could not create sanitizer", e);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private String identifier;
    private String file;
    private Language language;
    private char[] delimiters;
    private String[] skipSequences;
    private Class<? extends Sanitizer> sanitizerClass;

    private Builder() {
    }

    public DictionarySettings build() {
      Objects.requireNonNull(identifier, "identifier");
      Objects.requireNonNull(file, "file");
      Objects.requireNonNull(language, "language");

      return new DictionarySettings(
        identifier,
        file,
        language,
        firstNonNull(delimiters, new char[0]),
        firstNonNull(skipSequences, new String[0]),
        sanitizerClass);
    }

    public Builder identifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder file(String file) {
      this.file = file;
      return this;
    }

    public Builder language(Language language) {
      this.language = language;
      return this;
    }

    public Builder delimiters(char... delimiters) {
      this.delimiters = delimiters;
      return this;
    }

    public Builder skipSequences(String... skipSequences) {
      this.skipSequences = skipSequences;
      return this;
    }

    public Builder sanitizerClass(Class<? extends Sanitizer> sanitizerClass) {
      this.sanitizerClass = sanitizerClass;
      return this;
    }
  }
}
