package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.language.Language;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * Dictionary. Stores the {@link #file location} as well as various parameters
 * on its format so that its entries can be read correctly.
 */
@Getter
public class Dictionary {

  /**
   * The dictionary identifier is typically the ISO-639-1 abbreviation of its language.
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
  /**
   * Class of the sanitizer to use if a custom sanitizer should be used.
   */
  private final Class<? extends Sanitizer> sanitizerClass;

  @Getter(lazy = true)
  private final Sanitizer sanitizer = buildSanitizer();

  private Dictionary(String identifier, String file, Language language, char[] delimiters,
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

  private <T extends Sanitizer> T createSanitizer(Class<T> clazz) {
    try {
      Constructor<?> constructor = clazz.getDeclaredConstructor(Dictionary.class);
      return clazz.cast(constructor.newInstance(this));
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
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

    public Dictionary build() {
      Objects.requireNonNull(identifier, "identifier");
      Objects.requireNonNull(file, "file");
      Objects.requireNonNull(language, "language");

      return new Dictionary(
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
