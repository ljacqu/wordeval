package ch.jalu.wordeval.language;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Class with language-specific details, independent of any dictionary format.
 */
@Getter
@ToString(of = "code")
public class Language {

  private static final int ASCII_MAX_INDEX = 127;

  private final String code;
  private final String name;
  private final Alphabet alphabet;
  private final List<String> vowels;
  private final List<String> consonants;

  @Getter(lazy = true)
  private final Locale locale = buildLocale();
  @Getter(lazy = true)
  private final String charsToPreserve = computeCharsToPreserve();
  
  private Language(String code, String name, Alphabet alphabet, List<String> vowels, List<String> consonants) {
    this.code = code;
    this.name = name;
    this.alphabet = alphabet;
    this.vowels = vowels;
    this.consonants = consonants;
  }

  // --- Private members
  private Locale buildLocale() {
    return Locale.of(code);
  }
  
  /**
   * Returns the letters to preserve, i.e. the letters that should be recognized
   * as separate letters, e.g. "ä" in Swedish.
   *
   * @return the list of characters that are distinct letters
   */
  private String computeCharsToPreserve() {
    Set<String> standardLetters =
        Stream.concat(alphabet.getDefaultVowels().stream(), alphabet.getDefaultConsonants().stream())
            .collect(Collectors.toUnmodifiableSet());

    return Stream.concat(vowels.stream(), consonants.stream())
        .filter(letter -> !standardLetters.contains(letter))
        .filter(letter -> letter.length() == 1 && letter.charAt(0) > ASCII_MAX_INDEX)
        .collect(Collectors.joining());
  }

  // --- Builder
  public static Builder builder(String code, String name, Alphabet alphabet) {
    return new Builder(code, name, alphabet);
  }

  public static final class Builder {

    private final String code;
    private final String name;
    private final Alphabet alphabet;
    private String[] additionalVowels;
    private String[] additionalConsonants;
    private String[] lettersToRemove;

    private Builder(String code, String name, Alphabet alphabet) {
      this.code = requireNonNull(code, "code");
      this.name = requireNonNull(name, "name");
      this.alphabet = requireNonNull(alphabet, "alphabet");
    }

    public Language build() {
      return new Language(code, name, alphabet, buildVowels(), buildConsonants());
    }

    private List<String> buildVowels() {
      List<String> vowels = new ArrayList<>(alphabet.getDefaultVowels());
      if (lettersToRemove != null) {
        vowels.removeAll(asList(lettersToRemove));
      }
      if (additionalVowels != null) {
        vowels.addAll(asList(additionalVowels));
      }
      return vowels;
    }

    private List<String> buildConsonants() {
      List<String> consonants = new ArrayList<>(alphabet.getDefaultConsonants());
      if (lettersToRemove != null) {
        consonants.removeAll(asList(lettersToRemove));
      }
      if (additionalConsonants != null) {
        consonants.addAll(asList(additionalConsonants));
      }
      return consonants;
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
     * with the additional letters. Typically, if a letter is in this list, it
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
