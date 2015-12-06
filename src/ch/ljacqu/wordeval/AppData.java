package ch.ljacqu.wordeval;

import static ch.ljacqu.wordeval.language.Alphabet.CYRILLIC;
import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import ch.ljacqu.wordeval.dictionary.DictionarySettings;
import ch.ljacqu.wordeval.dictionary.FrSanitizer;
import ch.ljacqu.wordeval.dictionary.HuSanitizer;
import ch.ljacqu.wordeval.dictionary.Sanitizer;
import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;

import java.util.zip.CRC32;

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
    addLanguage("bg", CYRILLIC);
    addLanguage("cs", LATIN)
      // TODO #66: is stuff like "ň" really a distinct letter to preserve?
      .setAdditionalConsonants("č", "ď", "ch", "ň", "ř", "š", "ť", "ž")
      .setAdditionalVowels("á", "é", "ě", "í", "ó", "ú", "ů", "ý");
    addLanguage("da", LATIN)
      .setAdditionalVowels("æ", "ø", "å");
    // TODO #51: How to handle 'ß'?
    addLanguage("de", LATIN);
    addLanguage("en", LATIN)
      // TODO #66: how to deal with Y being consonant and vowel in English?
      .setAdditionalConsonants("y");
    addLanguage("es", LATIN)
      .setAdditionalConsonants("ñ");
    addLanguage("eu", LATIN)
      .setAdditionalConsonants("ñ");
    addLanguage("fi", LATIN)
      .setAdditionalVowels("ä", "ö");
    addLanguage("fr", LATIN);
    addLanguage("hu", LATIN)
      .setAdditionalConsonants("cs", "dz", "dzs", "gy", "ly", "ny", "sz", "ty", "zs")
      .setAdditionalVowels("á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű");
    addLanguage("it", LATIN);
    addLanguage("nb", LATIN)
      .setAdditionalVowels("æ", "ø", "å");
    addLanguage("nl", LATIN)
        .setAdditionalVowels("ij");
    addLanguage("nn", LATIN)
      .setAdditionalVowels(Language.get("nb").getAdditionalVowels());
    addLanguage("pl", LATIN)
      .setAdditionalConsonants("ć", "ł", "ń", "ś", "ź", "ż")
      .setAdditionalVowels("ą", "ę", "ó");
    addLanguage("pt", LATIN);
    addLanguage("ru", CYRILLIC);
    addLanguage("sr-cyrl", CYRILLIC);
    addLanguage("sr-latn", LATIN)
      .setAdditionalConsonants("č", "ć", "dž", "đ", "lj", "nj", "š", "ž");
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
    addDictionary("bg").setDelimiters('/');
    addDictionary("da").setDelimiters('/');
    addDictionary("de").setDelimiters('/');
    addDictionary("de-at").setDelimiters('/');
    addDictionary("de-ch").setDelimiters('/');
    addDictionary("de-de").setDelimiters('/');
    addDictionary("en-us").setDelimiters('/');
    addDictionary("en-test").setDelimiters('/');
    // TODO #62: Some Basque entries have _ but most parts seem to be present alone
    addDictionary("es").setDelimiters('/');
    addDictionary("eu").setDelimiters('/').setSkipSequences(".", "+", "_");
    addDictionary("fr", FrSanitizer.class);
    addDictionary("hu", HuSanitizer.class);
    addDictionary("it").setDelimiters('/');
    addDictionary("nb").setDelimiters('/');
    // TODO: The nl dictionary uses the digraph symbol 'ĳ' instead of 'i'+'j'
    addDictionary("nl").setDelimiters('/');
    addDictionary("nn").setDelimiters('/');
    addDictionary("pl").setDelimiters('/');
    addDictionary("pt-br").setDelimiters('/');
    addDictionary("pt-pt").setDelimiters('/', '[');
    addDictionary("ru").setDelimiters('/').setSkipSequences(".");
    addDictionary("sr-cyrl").setDelimiters();
    addDictionary("sr-latn").setDelimiters('/');
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
