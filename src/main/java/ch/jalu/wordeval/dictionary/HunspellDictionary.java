package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.dictionary.hunspell.sanitizer.HunspellSanitizer;
import ch.jalu.wordeval.language.Language;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Extension for dictionaries in the Hunspell format.
 */
@Getter
public final class HunspellDictionary extends Dictionary {

  private final HunspellSanitizer sanitizer;

  /**
   * @see #newHunspellDictionary
   */
  private HunspellDictionary(String identifier, String file, Language language, HunspellSanitizer sanitizer) {
    super(identifier, file, language);
    this.sanitizer = sanitizer;
  }

  /**
   * @return path to the .aff file associated with the dictionary
   */
  public String getAffixFile() {
    return StringUtils.substringBeforeLast(getFile(), ".") + ".aff";
  }

  /**
   * Returns a new builder to create a Hunspell dictionary object.
   *
   * @param identifier the identifier (language code) of the dictionary
   * @return new builder
   */
  public static Builder newHunspellDictionary(String identifier) {
    Builder builder = new Builder();
    builder.identifier = identifier;
    return builder;
  }

  public static final class Builder {

    private String identifier;
    private String file;
    private Language language;
    private HunspellSanitizer sanitizer;

    private Builder() {
    }

    /**
     * Sets the file location of the dictionary (.dic file).
     *
     * @param file path to the dictionary file
     * @return this builder
     */
    public Builder file(String file) {
      this.file = file;
      return this;
    }

    /**
     * Sets the language the dictionary is for.
     *
     * @param language the language the dictionary is for
     * @return this builder
     */
    public Builder language(Language language) {
      this.language = language;
      return this;
    }

    /**
     * Sets the sanitizer to use while loading the dictionary.
     *
     * @param sanitizer the sanitizer to use
     * @return this builder
     */
    public Builder sanitizer(HunspellSanitizer sanitizer) {
      this.sanitizer = sanitizer;
      return this;
    }

    /**
     * Sets a new default sanitizer to use while loading the dictionary. All lines containing
     * any of the provided sequences will be skipped when loading.
     *
     * @param skipSequences strings to search for to skip lines while loading the dictionary
     * @return this builder
     */
    public Builder sanitizer(String... skipSequences) {
      this.sanitizer = new HunspellSanitizer(skipSequences);
      return this;
    }

    /**
     * Builds a new dictionary object with the data provided to the builder.
     *
     * @return new hunspell dictionary object
     */
    public HunspellDictionary build() {
      Objects.requireNonNull(identifier, "identifier");
      Objects.requireNonNull(file, "file");
      Objects.requireNonNull(language, "language");
      sanitizer = sanitizer == null ? new HunspellSanitizer() : sanitizer;

      return new HunspellDictionary(identifier, file, language, sanitizer);
    }
  }
}
