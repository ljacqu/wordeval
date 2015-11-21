package ch.ljacqu.wordeval;

import static ch.ljacqu.wordeval.language.Alphabet.CYRILLIC;
import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import ch.ljacqu.wordeval.dictionary.DictionarySettings;
import ch.ljacqu.wordeval.dictionary.FrSanitizer;
import ch.ljacqu.wordeval.dictionary.HuSanitizer;
import ch.ljacqu.wordeval.dictionary.Sanitizer;
import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;

/**
 * Application data holder.
 */
public final class AppData {

  private AppData() {
  }

  /**
   * Initializes the settings for dictionaries and their language.
   */
  public static void init() {
    initLanguages();
    initDictionaries();
  }

  /**
   * Initializes languages.
   */
  private static void initLanguages() {
    addLanguage("af", LATIN);
    addLanguage("cs", LATIN)
      // TODO #66: is stuff like "ň" really a distinct letter to preserve?
      .setAdditionalConsonants("č", "ď", "ch", "ň", "ř", "š", "ť", "ž")
      .setAdditionalVowels("á", "é", "ě", "í", "ó", "ú", "ů", "ý");
    addLanguage("en", LATIN)
      // TODO #66: how to deal with Y being consonant and vowel in English?
      .setAdditionalConsonants("y");
    addLanguage("eu", LATIN)
      .setAdditionalConsonants("ñ");
    addLanguage("fr", LATIN);
    addLanguage("hu", LATIN)
      .setAdditionalConsonants("cs", "dz", "dzs", "gy", "ly", "ny", "sz", "ty", "zs")
      .setAdditionalVowels("á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű");
    addLanguage("ru", CYRILLIC);
    addLanguage("tr", LATIN)
      .setAdditionalConsonants("ç", "ğ", "ş", "y")
      .setAdditionalVowels("ı", "ö", "ü")
      .setLettersToRemove("y");
  }
    
  /**
   * Initializes dictionaries.
   */
  private static void initDictionaries() {
    addDictionary("af").setDelimiters('/').setSkipSequences(".", "µ", "Ð", "ø");
    addDictionary("en-us").setDelimiters('/');
    addDictionary("en-test").setDelimiters('/');
    // TODO #62: Some Basque entries have _ but most parts seem to be present alone
    addDictionary("eu").setDelimiters('/').setSkipSequences(".", "+", "_");
    addDictionary("fr", FrSanitizer.class);
    addDictionary("hu", HuSanitizer.class);
    addDictionary("ru").setDelimiters('/').setSkipSequences(".");
    addDictionary("tr").setDelimiters(' ');
  }
  
  private static Language addLanguage(String code, Alphabet alphabet) {
    Language language = new Language(code, alphabet);
    Language.add(language);
    return language;
  }  
  
  private static DictionarySettings addDictionary(String code) {
    return DictionarySettings.add(code);
  }
  
  private static void addDictionary(String code, Class<? extends Sanitizer> sanitizerClass) {
    DictionarySettings.add(code, sanitizerClass);
  }
}
