package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class with language-specific details, independent of any dictionary format.
 */
public final class Language {

  private static Map<String, Language> languages = new HashMap<>();

  private String code;
  private Alphabet alphabet;
  private String[] additionalVowels = {};
  private String[] additionalConsonants = {};

  // List of language data
  static {
    add("af", LATIN);
    add("cs", LATIN)
      // TODO: is stuff like "ň" really a distinct letter to preserve?
      .setAdditionalConsonants("č", "ď", "ch", "ň", "ř", "š", "ť", "ž")
      .setAdditionalVowels("á", "é", "ě", "í", "ó", "ú", "ů", "ý");
    add("en", LATIN)
      // TODO: how to deal with Y being consonant and vowel in English?
      .setAdditionalConsonants("y");
    add("hu", LATIN)
      .setAdditionalConsonants("cs", "dz", "dzs", "gy", "ly", "ny", "sz", "ty", "zs")
      .setAdditionalVowels("á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű");
    add("tr", LATIN)
      // TODO #29: Remove 'y' as vowel in Turkish
      .setAdditionalConsonants("ç", "ğ", "ş", "y")
      .setAdditionalVowels("ı", "ö", "ü");
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

  private static Language add(String code, Alphabet alphabet) {
    Language language = new Language(code, alphabet);
    languages.put(language.code, language);
    return language;
  }

  /**
   * Creates a new Language instance.
   * @param code The ISO-639-1 code of the language
   */
  public Language(String code, Alphabet alphabet) {
    this.code = code;
    this.alphabet = alphabet;
  }

  /**
   * Returns the ISO-639-1 code of the given language.
   * @return The language code
   */
  public String getCode() {
    return code;
  }

  public Alphabet getAlphabet() {
    return alphabet;
  }

  /**
   * Creates a {@link Locale} object for the given language.
   * @return Locale object for language
   */
  public Locale buildLocale() {
    return new Locale(code);
  }

  // --- Additional vowels
  /**
   * Sets the list of additional vowels which should be recognized as fully
   * distinct letters aside from the usual a, e, i, o and u. Add additional
   * characters (e.g. da "ø") as well as special vowels consisting of multiple
   * characters as per the language's rules, e.g. nl "ij" if desired. The
   * entries should be supplied all in lower-case.
   * @param vowels The list of additional vowels to recognize
   * @return The Language object
   */
  public Language setAdditionalVowels(String... vowels) {
    additionalVowels = vowels;
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
    return this;
  }

  public String[] getAdditionalConsonants() {
    return additionalConsonants;
  }

}
