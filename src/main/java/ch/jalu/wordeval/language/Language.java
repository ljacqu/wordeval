package ch.jalu.wordeval.language;

import lombok.Getter;

import java.util.Locale;
import java.util.Objects;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * Class with language-specific details, independent of any dictionary format.
 */
@Getter
public class Language {

  private static final int ASCII_MAX_INDEX = 127;

  private final String code;
  private final String name;
  private final Alphabet alphabet;
  private final String[] additionalVowels;
  private final String[] additionalConsonants;
  private final String[] lettersToRemove;

  @Getter(lazy = true)
  private final Locale locale = buildLocale();
  @Getter(lazy = true)
  private final String charsToPreserve = computeCharsToPreserve();
  
  private Language(String code, String name, Alphabet alphabet, String[] additionalVowels,
                   String[] additionalConsonants, String[] lettersToRemove) {
    this.code = code;
    this.name = name;
    this.alphabet = alphabet;
    this.additionalVowels = additionalVowels;
    this.additionalConsonants = additionalConsonants;
    this.lettersToRemove = lettersToRemove;
  }

  
  // --- Private members
  private Locale buildLocale() {
    return new Locale(code);
  }
  
  /**
   * Returns the letters to preserve, i.e. the letters that should be recognized
   * as separate letters, e.g. "ä" in Swedish.
   *
   * @return the list of characters that are distinct letters
   */
  private String computeCharsToPreserve() {
    StringBuilder sb = new StringBuilder();
    for (String letter : getAdditionalConsonants()) {
      if (letter.length() == 1 && letter.charAt(0) > ASCII_MAX_INDEX) {
        sb.append(letter);
      }
    }
    for (String letter : getAdditionalVowels()) {
      if (letter.length() == 1 && letter.charAt(0) > ASCII_MAX_INDEX) {
        sb.append(letter.charAt(0));
      }
    }
    return sb.toString();
  }

  // --- Builder
  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private String code;
    private String name;
    private Alphabet alphabet;
    private String[] additionalVowels;
    private String[] additionalConsonants;
    private String[] lettersToRemove;

    private Builder() {
    }

    public Language build() {
      Objects.requireNonNull(code, "code");
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(alphabet, "alphabet");

      return new Language(
        code,
        name,
        alphabet,
        firstNonNull(additionalVowels,     new String[0]),
        firstNonNull(additionalConsonants, new String[0]),
        firstNonNull(lettersToRemove,      new String[0]));
    }

    public Builder code(String code) {
      this.code = code;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder alphabet(Alphabet alphabet) {
      this.alphabet = alphabet;
      return this;
    }

    /**
     * Sets the list of additional vowels which should be recognized as fully
     * distinct letters aside from the usual a, e, i, o and u. Add additional
     * characters (e.g. da "ø") as well as special vowels consisting of multiple
     * characters as per the language's rules, e.g. nl "ij" if desired. The
     * entries should be supplied all in lower-case.
     *
     * @param vowels the list of additional vowels to recognize
     * @return the builder
     */
    public Builder additionalVowels(String... vowels) {
      additionalVowels = vowels;
      return this;
    }

    /**
     * Sets the list of additional consonants that should be recognized as fully
     * distinct letters aside from the typical consonants in a-z (e.g. Icelandic
     * þ). The entries should be supplied all in lower-case.
     *
     * @param consonants the list of additional consonants to recognize
     * @return the builder
     */
    public Builder additionalConsonants(String... consonants) {
      additionalConsonants = consonants;
      return this;
    }

    /**
     * Sets the list of letters to remove from the standard vowel or consonant
     * list. This has no effect on the additional vowels and consonant list but
     * removes vowels or consonants from the default a-z list that is augmented
     * with the additional letters. Typically if a letter is in this list, it
     * should be in one of the "additional" lists.
     *
     * @param letters the letters to remove from default lists
     * @return the builder
     */
    public Builder lettersToRemove(String... letters) {
      lettersToRemove = letters;
      return this;
    }
  }

}
