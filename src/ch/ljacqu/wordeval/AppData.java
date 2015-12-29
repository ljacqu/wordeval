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
    addLanguage("af", "Afrikaans", LATIN);
    addLanguage("bg", "Bulgarian", CYRILLIC);
    addLanguage("cs", "Czech", LATIN)
      // TODO #66: is stuff like "ň" really a distinct letter to preserve?
      .setAdditionalConsonants("č", "ď", "ch", "ň", "ř", "š", "ť", "ž")
      .setAdditionalVowels("á", "é", "ě", "í", "ó", "ú", "ů", "ý");
    addLanguage("da", "Danish", LATIN)
      .setAdditionalVowels("æ", "ø", "å");
    // TODO #51: How to handle 'ß'?
    addLanguage("de", "German", LATIN);
    addLanguage("en", "English", LATIN)
      // TODO #66: how to deal with Y being consonant and vowel in English?
      .setAdditionalConsonants("y");
    addLanguage("es", "Spanish", LATIN)
      .setAdditionalConsonants("ñ");
    addLanguage("eu", "Basque", LATIN)
      .setAdditionalConsonants("ñ");
    addLanguage("fi", "Finnish", LATIN)
      .setAdditionalVowels("ä", "ö");
    addLanguage("fr", "French", LATIN);
    addLanguage("hu", "Hungarian", LATIN)
      .setAdditionalConsonants("cs", "dz", "dzs", "gy", "ly", "ny", "sz", "ty", "zs")
      .setAdditionalVowels("á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű");
    addLanguage("it", "Italian", LATIN);
    addLanguage("nb", "Norwegian (Bokmål)", LATIN)
      .setAdditionalVowels("æ", "ø", "å");
    addLanguage("nl", "Dutch", LATIN)
        .setAdditionalVowels("ij");
    addLanguage("nn", "Norwegian (Nynorsk)", LATIN)
      .setAdditionalVowels(Language.get("nb").getAdditionalVowels());
    addLanguage("pl", "Polish", LATIN)
      .setAdditionalConsonants("ć", "ł", "ń", "ś", "ź", "ż")
      .setAdditionalVowels("ą", "ę", "ó");
    addLanguage("pt", "Portuguese", LATIN);
    addLanguage("ru", "Russian", CYRILLIC);
    addLanguage("sr-cyrl", "Serbian (Cyrillic)", CYRILLIC);
    addLanguage("sr-latn", "Serbian (Latin)", LATIN)
      .setAdditionalConsonants("č", "ć", "dž", "đ", "lj", "nj", "š", "ž");
    addLanguage("tr", "Turkish", LATIN)
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
  
  private static Language addLanguage(String code, String name, Alphabet alphabet) {
    Language language = new Language(code, name, alphabet);
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
