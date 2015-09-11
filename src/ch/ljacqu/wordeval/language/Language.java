package ch.ljacqu.wordeval.language;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Class with language-specific details, independent of any dictionary format.
 */
public final class Language {

  private static Map<String, Language> languages = new HashMap<>();

  private String code;
  private Set<Character> lettersToPreserve = new HashSet<>();
  private String[] additionalVowels;
  private String[] additionalConsonants;

  // List of language data
  static {
    // TODO #7: Add additional letters (Hungarian not complete!)
    add(new Language("af"));
    add(new Language("hu").setAdditionalConsonants("cs", "dzs", "hy", "ny"));
    add(new Language("tr"));
  }

  /**
   * Gets the settings of a language by its ISO-639-1 abbreviation.
   * @param code The code of the language
   * @return Language object for the given language
   */
  public static Language get(String code) {
    Language language = languages.get(code);
    if (language == null) {
      throw new IllegalArgumentException("Language with code '" + code
          + "' is unknown");
    }
    return languages.get(code);
  }

  private static void add(Language language) {
    languages.put(language.code, language);
  }

  /**
   * Creates a new Language instance.
   * @param code The ISO-639-1 code of the language
   */
  public Language(String code) {
    this.code = code;
  }

  /**
   * Returns the ISO-639-1 code of the given language
   * @return The language code
   */
  public String getCode() {
    return code;
  }

  /**
   * Creates a {@link Locale} object for the given language
   * @return Locale object for language
   */
  public Locale buildLocale() {
    return new Locale(code);
  }

  // --- Additional vowels
  /**
   * Set the list of additional vowels which should be recognized as fully
   * distinct letters aside from the usual a, e, i, o and u. Only the lower-case
   * versions of the custom vowels are required. Add additional characters (e.g.
   * da "ø") as well as special vowels consisting of multiple characters as per
   * the language's rules, e.g. nl "ij" if desired. The entries should be
   * supplied all in lower-case.
   * @param vowels The list of additional vowels to recognize
   * @return The Language object
   */
  public Language setAdditionalVowels(String... vowels) {
    additionalVowels = vowels;
    addSingleLettersToPreserveList(vowels);
    return this;
  }

  public String[] getAdditionalVowels() {
    return additionalVowels;
  }

  // --- Additional consonants
  /**
   * Sets the list of additional consonants that should be recognized as fully
   * distinct letters aside from the typical consonants in a-z (e.g. Icelandic
   * þ). The entries should be supplied all in lower-case.
   * @param consonants The list of additional consonants to recognize
   * @return The Language object
   */
  public Language setAdditionalConsonants(String... consonants) {
    additionalConsonants = consonants;
    addSingleLettersToPreserveList(consonants);
    return this;
  }

  public String[] getAdditionalConsonants() {
    return additionalConsonants;
  }

  // --- Letters to preserve
  /**
   * Returns the list of additional characters that should be preserved in a
   * language's words as they are considered distinct letters. This can be
   * additional letters such as Icelandic þ, or Swedish ö, in which language it
   * is considered a separate distinct vowel (as opposed to German).
   * @return List of additional characters to preserve in the NO_ACCENTS word
   *         form
   */
  public char[] getLettersToPreserve() {
    return ArrayUtils.toPrimitive(lettersToPreserve
        .toArray(new Character[lettersToPreserve.size()]));
  }

  /**
   * Adds the entries of a string array to the list of letters to preserve that
   * consist of one character and have an ASCII code above 127.
   * @param lettersArray
   */
  private void addSingleLettersToPreserveList(String[] lettersArray) {
    for (String letter : lettersArray) {
      if (letter.length() == 1 && letter.charAt(0) > 127) {
        lettersToPreserve.add(letter.charAt(0));
      }
    }
  }
}
