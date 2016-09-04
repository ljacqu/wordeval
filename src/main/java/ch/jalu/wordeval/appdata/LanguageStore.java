package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;

/**
 * Stores {@link Language} objects.
 */
public class LanguageStore extends ObjectStore<String, Language> {

  public LanguageStore() {
    initLanguages();
  }

  Language getLanguageOrNull(String code) {
    return entries.get(code);
  }

  @Override
  protected String getKey(Language language) {
    return language.getCode();
  }

  /**
   * Initializes languages.
   */
  private void initLanguages() {
    addLanguage("af", "Afrikaans", Alphabet.LATIN);
    addLanguage("bg", "Bulgarian", Alphabet.CYRILLIC);
    addLanguage("cs", "Czech", Alphabet.LATIN)
        // TODO #66: is stuff like "ň" really a distinct letter to preserve?
        .setAdditionalConsonants("č", "ď", "ch", "ň", "ř", "š", "ť", "ž")
        .setAdditionalVowels("á", "é", "ě", "í", "ó", "ú", "ů", "ý");
    addLanguage("da", "Danish", Alphabet.LATIN)
        .setAdditionalVowels("æ", "ø", "å");
    // TODO #51: How to handle 'ß'?
    addLanguage("de", "German", Alphabet.LATIN);
    addLanguage("en", "English", Alphabet.LATIN)
        // TODO #66: how to deal with Y being consonant and vowel in English?
        .setAdditionalConsonants("y");
    addLanguage("es", "Spanish", Alphabet.LATIN)
        .setAdditionalConsonants("ñ");
    addLanguage("eu", "Basque", Alphabet.LATIN)
        .setAdditionalConsonants("ñ");
    addLanguage("fi", "Finnish", Alphabet.LATIN)
        .setAdditionalVowels("ä", "ö");
    addLanguage("fr", "French", Alphabet.LATIN);
    addLanguage("hu", "Hungarian", Alphabet.LATIN)
        .setAdditionalConsonants("cs", "dz", "dzs", "gy", "ly", "ny", "sz", "ty", "zs")
        .setAdditionalVowels("á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű");
    addLanguage("it", "Italian", Alphabet.LATIN);
    addLanguage("nb", "Norwegian (Bokmål)", Alphabet.LATIN)
        .setAdditionalVowels("æ", "ø", "å");
    addLanguage("nl", "Dutch", Alphabet.LATIN)
        .setAdditionalVowels("ij");
    addLanguage("nn", "Norwegian (Nynorsk)", Alphabet.LATIN)
        .setAdditionalVowels("æ", "ø", "å");
    addLanguage("pl", "Polish", Alphabet.LATIN)
        .setAdditionalConsonants("ć", "ł", "ń", "ś", "ź", "ż")
        .setAdditionalVowels("ą", "ę", "ó");
    addLanguage("pt", "Portuguese", Alphabet.LATIN);
    addLanguage("ru", "Russian", Alphabet.CYRILLIC);
    addLanguage("sr-cyrl", "Serbian (Cyrillic)", Alphabet.CYRILLIC);
    addLanguage("sr-latn", "Serbian (Latin)", Alphabet.LATIN)
        .setAdditionalConsonants("č", "ć", "dž", "đ", "lj", "nj", "š", "ž");
    addLanguage("tr", "Turkish", Alphabet.LATIN)
        .setAdditionalConsonants("ç", "ğ", "ş", "y")
        .setAdditionalVowels("ı", "ö", "ü")
        .setLettersToRemove("y");
  }

  private Language addLanguage(String code, String name, Alphabet alphabet) {
    Language language = new Language(code, name, alphabet);
    add(language);
    return language;
  }

}
