package ch.ljacqu.wordeval.language;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.Getter;

/**
 * Class with language-specific details, independent of any dictionary format.
 */
@Getter
public final class Language {

  private static Map<String, Language> languages = new HashMap<>();
  private static final int ASCII_MAX_INDEX = 127;

  private final String code;
  private final Alphabet alphabet;
  @Getter(lazy = true)
  private final Locale locale = buildLocale();
  private String[] additionalVowels = {};
  private String[] additionalConsonants = {};
  private String[] lettersToRemove = {};
  @Getter(lazy = true)
  private final String charsToPreserve = computeCharsToPreserve();
  
  /**
   * Creates a new Language instance.
   * @param code the ISO-639-1 code of the language
   * @param alphabet the alphabet of the language
   */
  public Language(String code, Alphabet alphabet) {
    this.code = code;
    this.alphabet = alphabet;
  }

  /**
   * Gets the settings of a language by its ISO-639-1 abbreviation.
   * @param code the code of the language
   * @return Language object for the given language
   */
  public static Language get(String code) {
    Language language = languages.get(code);
    if (language == null) {
      if (code.indexOf('-') != -1) {
        return get(code.substring(0, code.indexOf('-')));
      }
      throw new IllegalArgumentException("Language '" + code + "' unknown");
    }
    return languages.get(code);
  }

  /**
   * Adds a new language to the list of known languages.
   * @param language the Language object to add
   */
  public static void add(Language language) {
    languages.put(language.code, language);
  }

  // --- Additional vowels
  /**
   * Sets the list of additional vowels which should be recognized as fully
   * distinct letters aside from the usual a, e, i, o and u. Add additional
   * characters (e.g. da "ø") as well as special vowels consisting of multiple
   * characters as per the language's rules, e.g. nl "ij" if desired. The
   * entries should be supplied all in lower-case.
   * @param vowels the list of additional vowels to recognize
   * @return the Language object
   */
  public Language setAdditionalVowels(String... vowels) {
    additionalVowels = vowels;
    return this;
  }

  // --- Additional consonants
  /**
   * Sets the list of additional consonants that should be recognized as fully
   * distinct letters aside from the typical consonants in a-z (e.g. Icelandic
   * þ). The entries should be supplied all in lower-case.
   * @param consonants the list of additional consonants to recognize
   * @return the Language object
   */
  public Language setAdditionalConsonants(String... consonants) {
    additionalConsonants = consonants;
    return this;
  }

  // --- Letters to remove
  /**
   * Sets the list of letters to remove from the standard vowel or consonant
   * list. This has no effect on the additional vowels and consonant list but
   * removes vowels or consonants from the default a-z list that is augmented
   * with the additional letters. Typically if a letter is in this list, it
   * should be in one of the "additional" lists.
   * @param letters the letters to remove from default lists
   * @return the Language object
   */
  public Language setLettersToRemove(String... letters) {
    lettersToRemove = letters;
    return this;
  }
  
  // --- Private members
  private Locale buildLocale() {
    return new Locale(code);
  }
  
  /**
   * Returns the letters to preserve, i.e. the letters that should be recognized
   * as separate letters, e.g. "ä" in Swedish.
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

}
